# Date: 13.04.2016
# Author: Glaukon Ariston
# About:
#	Execute an application with API hooks that prevent it from stealing window focus.
#
#	Based on StackOverflow answers by:
#	0xC0000022L
#	http://reverseengineering.stackexchange.com/questions/1979/how-do-i-see-the-parameters-passed-to-regopenkeyex-and-set-a-conditional-breakp/1981#1981
#	Oliver Salzburg
#	http://superuser.com/questions/18383/preventing-applications-from-stealing-focus


import sys
import ctypes
try:
	from pydbg import *
	from pydbg.defines import *
	import hooking
except:
	print "ERROR: you need pydbg and utils.hooking from PAIMEI."
	sys.exit(-1)



#================================================================= Hook Handlers
hooks = None

# Hooks
# http://stackoverflow.com/questions/4292447/does-ret-instruction-cause-esp-register-added-by-4
'''
e.g. Stack layout for FlashWindow:
stack       address
=====       =======
****        esp+3*4
bInvert     esp+2*4
hWnd        esp+1*4
eip         esp
'''
def ret_imm8(dbg, argn, eax):
	eip = dbg.get_arg(0)
	dbg.set_register('EAX', eax)
	imm8 = (argn+1)*4
	dbg.set_register('ESP', dbg.context.Esp + imm8)
	dbg.set_register('EIP', eip)
	#print 'RET %d -> %LX' % (imm8, eip)


'''
BOOL WINAPI
FlashWindow(
  __in  HWND hWnd,
  __in  BOOL bInvert
  ) {
  return 0;
}
'''
def entry_FlashWindow(dbg, hWnd, bInvert):
	#print 'FlashWindow hWnd:%LX bInvert:%d' % (hWnd, bInvert)
	ret_imm8(dbg, 2, 0)
	return DBG_CONTINUE


'''
BOOL WINAPI 
FlashWindowEx(
  __in  PFLASHWINFO pfwi
  ) {
  return 0;
}
'''
def entry_FlashWindowEx(dbg, pfwi):
	#print 'FlashWindowEx pfwi:%LX' % (pfwi,)
	ret_imm8(dbg, 1, 0)
	return DBG_CONTINUE


'''
BOOL WINAPI 
SetForegroundWindow(
  __in  HWND hWnd
  ) {
  // Pretend window was brought to foreground
  return 1;
}
'''
def entry_SetForegroundWindow(dbg, hWnd):
	#print 'SetForegroundWindow hWnd:%LX' % (hWnd,)
	ret_imm8(dbg, 1, 1)
	return DBG_CONTINUE


'''
BOOL WINAPI 
LockSetForegroundWindow(
	_In_ UINT uLockCode
) {
  // Disables calls to SetForegroundWindow.
  return 1; // LSFW_LOCK;
}
'''
def entry_LockSetForegroundWindow(dbg, uLockCode):
	#print 'LockSetForegroundWindow uLockCode:%d' % (uLockCode,)
	ret_imm8(dbg, 1, 1)
	return DBG_CONTINUE


FUNCTIONS_TO_HOOK = {
	"user32.dll": {
		"FlashWindow": { "args": 2, "entry": entry_FlashWindow, "exit": None },
		"FlashWindowEx": { "args": 1, "entry": entry_FlashWindowEx, "exit": None },
		"SetForegroundWindow": { "args": 1, "entry": entry_SetForegroundWindow, "exit": None },
		"LockSetForegroundWindow": { "args": 1, "entry": entry_LockSetForegroundWindow, "exit": None },
	},
}



#================================================================= Debugger
class Debugger(pydbg):
	@staticmethod
	def __getlen(mbi, address):
		# What's the maximum number of bytes we can read?
		_maxlen = 64*1024
		absmaxlen = (mbi.BaseAddress + mbi.RegionSize) - address
		if absmaxlen > _maxlen:
			return _maxlen
		return absmaxlen


	def readmem(self, address, len = 0):
		try:
			mbi = self.virtual_query(address)
		except:
			return None, "%08X <invalid ptr>" % (address)

		if mbi.Protect & PAGE_GUARD: # no way to display contents of a guard page
			return None, "%08X <guard page>" % (address)

		if 0 == len: # try to make a good guess then
			len = self.__getlen(mbi, address)

		try:
			explored = self.read_process_memory(address, len)
		except:
			return None, "%08X <ReadProcessMemory failed>" % (address)

		return explored, None


	def readstring(self, address, unicodeHint = False, returnNone = False):
		if 0 == address:
			if returnNone:
				return None
			return "<nullptr>"

		explored, retval = self.readmem(address)

		if not explored:
			if returnNone:
				return None
			return retval

		explored_string = None

		if not unicodeHint:
			explored_string = self.get_ascii_string(explored)

		if not explored_string:
			explored_string = self.get_unicode_string(explored)

		if not explored_string:
			explored_string = self.get_printable_string(explored)

		return explored_string


#================================================================= Hooks
class Hooks:
	hooked = {}
	hookcont = None
	dbg = None
	fct2hook = None

	def __init__ (self, dbg, fct2hook):
		self.hookcont = hooking.hook_container()
		self.hooked = {}
		self.dbg = dbg
		self.fct2hook = fct2hook
		dbg.set_callback(LOAD_DLL_DEBUG_EVENT, self.handler_loadDLL)

	def hookByDLL(self, dll):
		if not dll.name.lower() in self.hooked:
			for key,value in self.fct2hook.items():
				if key.lower() == dll.name.lower():
					self.hooked[dll.name.lower()] = 1
					#print "%s at %08x" % (dll.name, dll.base)
					for func,fctprops in value.items():
						entry = None; exit = None; args = 0
						if "entry" in fctprops and None != fctprops["entry"]:
							#print "\tentry hook " + func
							entry = fctprops["entry"]
						if "exit" in fctprops and None != fctprops["exit"]:
							#print "\texit hook " + func
							exit = fctprops["exit"]
						if "args" in fctprops and None != fctprops["args"]:
							args = fctprops["args"]
						if None != entry or None != exit:
							funcaddr = self.dbg.func_resolve(dll.name, func)
							self.hookcont.add(self.dbg, funcaddr, args, entry, exit)
		else:
			self.hooked[dll.name.lower()] += 1
		return

	@staticmethod
	def handler_loadDLL(dbg):
		global hooks
		dbg.hide_debugger()
		last_dll = dbg.get_system_dll(-1)
		hooks.hookByDLL(last_dll)
		return DBG_CONTINUE


#================================================================= main
'''
\app\edit\010Editor_v6.0.3\010Editor.exe -noui -nowarnings
user32.dll at 767e0000
		entry hook SetForegroundWindow
		entry hook FlashWindow
		entry hook LockSetForegroundWindow
		entry hook FlashWindowEx
SetForegroundWindow hWnd:26A04CE
SetForegroundWindow hWnd:26A04CE
FlashWindowEx pfwi:33F8A0
'''
def defendFocus(app, params):
	#print '%s %s' % (app, params)
	dbg = Debugger()
	dbg.load(app, params)
	global hooks
	hooks = Hooks(dbg, FUNCTIONS_TO_HOOK)
	dbg.run()


if __name__ == "__main__":
	from defines import APP_010EDITOR
	# -noui -nowarnings -exit
	params = r'''-noui -nowarnings -exit'''
	defendFocus(APP_010EDITOR, params)


