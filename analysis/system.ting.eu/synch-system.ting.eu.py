# -*- coding: utf-8 -*-
"""
Date: 26.02.2016
Author: Glaukon Ariston
Abstract:
	Synch local copy of the Ting books repository found at http://system.ting.eu/book-files.

https://forum.ubuntuusers.de/topic/synchronisation-von-ting-dem-hoerstift-von-bro/
https://uli.popps.org/2013/12/ting-und-linux/
http://marc.info/?l=linux-hotplug&m=137486852107741&w=2
http://www.ting-el-tangel.de/doku.php?id=nuetzliches
https://help.ubuntu.com/community/Beginners/BashScripting
"""
import urllib
import os
import re
import codecs
import hashlib
import ctypes
import time, datetime
import glob
import mmap

MAX_ERRORS = 8
DELAY = 5
RE_OPTIONS = re.LOCALE | re.UNICODE | re.DOTALL | re.IGNORECASE | re.MULTILINE
RE_MD5_ENTRY = re.compile(ur'^(.+)MD5: ([0-9,a-f]+)', re.LOCALE | re.UNICODE | re.IGNORECASE)
RE_FILENAME = re.compile(ur'^([0-9]+)_(.+)\.(.+)$', re.LOCALE | re.UNICODE | re.IGNORECASE)

TINGPATH = 'books'
URL = "system.ting.eu/book-files"
FILE_TYPES = ["Thumb", "File", "Script"]

LOCAL_PATH = {}
LOCAL_PATH["Desc"] = TINGPATH + "/%s_en.txt"
LOCAL_PATH["Thumb"] = TINGPATH + "/%s_en.png"
LOCAL_PATH["File"] = TINGPATH + "/%s_en.ouf"
LOCAL_PATH["Script"] = TINGPATH + "/%s_en.src"

REMOTE_PATH = {}
REMOTE_PATH["Desc"] = "http://"+URL+"/get-description/id/%s/area/en"
REMOTE_PATH["Thumb"] = "http://"+URL+"/get/id/%s/area/en/type/thumb"
REMOTE_PATH["File"] = "http://"+URL+"/get/id/%s/area/en/type/archive"
REMOTE_PATH["Script"] = "http://"+URL+"/get/id/%s/area/en/type/script"


def timestamp():
	#FORMAT = '%Y-%m-%d %H:%M:%S'
	FORMAT = '%H:%M:%S'
	ts = time.time()
	return datetime.datetime.fromtimestamp(ts).strftime(FORMAT)


def fullUrl(url, params):
	key = url
	if params:
		ps = urllib.urlencode(dict([k, v.encode('utf-8')] for k, v in params.items()))
		key = "?".join((url, ps))
	return key


# Downloading a File Protected by NTLM/SSPI Without Prompting For Credentials Using Python on Win32?
# http://stackoverflow.com/questions/2149496/downloading-a-file-protected-by-ntlm-sspi-without-prompting-for-credentials-usin
# How To Download a File Without Prompting
# http://support.microsoft.com/kb/244757
def downloadIE(url, params, filepath):
	url = fullUrl(url, params)
	for i in range(MAX_ERRORS):
		if isinstance(url, str):
			ret = ctypes.windll.urlmon.URLDownloadToFileA(0, url, filepath, 0, 0)
		elif isinstance(url, unicode):
			ret = ctypes.windll.urlmon.URLDownloadToFileW(0, url, unicode(filepath), 0, 0)
		if ret == 0:
		   return
		else:
			print "ERROR: URLDownloadToFile %08X" % (ret)
			time.sleep(pow(2, i+1)*DELAY)

	raise Exception('URLDownloadToFile', ret)


def downloadUrl(url, filepath):
	print 'url %s' % url
	downloadIE(url, None, filepath)


def checksumCheck(filepath, md5digest):
	md5 = hashlib.md5()
	with open(filepath, "rb") as f:
		CHUNK_SIZE = 10*1024*1024
		fileSize = os.fstat(f.fileno()).st_size
		for offset in range(0, fileSize, CHUNK_SIZE):
			length = CHUNK_SIZE if offset + CHUNK_SIZE <= fileSize else fileSize - offset
			mm = mmap.mmap(f.fileno(), length, access=mmap.ACCESS_READ, offset=offset)
			md5.update(mm)
			mm.close()
		return md5digest == md5.hexdigest()


def synchFile(type, bookId, md5digest=None):
	filepath = LOCAL_PATH[type] % bookId
	if os.path.exists(filepath):
		if not md5digest:
			return filepath
		else:
			if checksumCheck(filepath, md5digest):
				return filepath
			else:
				print 'Book %s, Type %s: Digest mismatch: %s' % (bookId, type, md5digest)

	url = REMOTE_PATH[type] % bookId
	downloadUrl(url, filepath)
	return filepath


def extractChecksums(descFile):
	with codecs.open(descFile, 'r', 'UTF-8') as f:
		md5dict = {}
		for line in f.readlines():
			m = RE_MD5_ENTRY.match(line)
			if m:
				(key, md5digest) = m.group(1,2)
				md5dict[key] = md5digest.strip()
		return md5dict


def synchBook(bookId):
	print 'Book %s' % bookId
	descFile = synchFile('Desc', bookId)
	for type, md5digest in extractChecksums(descFile).iteritems():
		synchFile(type, bookId, md5digest)


def integrityCheck(dir):
	print "Running Ting book integrity checks in %s" % (dir,)
	corrupt = []
	for descFile in glob.glob('%s/*.txt' % (dir,)):
		m = RE_FILENAME.match(os.path.basename(descFile))
		(bookId, lang, ext) = m.group(1,2,3)
		print '%s %s' % (timestamp(), bookId)
		descMap = extractChecksums(descFile)
		if 0 == len(descMap.keys()):
			print 'Book %s, Missing description (txt) file' % (bookId)
			corrupt.append((bookId, 'Desc'))
		else:
			for type, md5digest in descMap.iteritems():
				filepath = LOCAL_PATH[type] % bookId
				if not os.path.exists(filepath):
					print 'Book %s, Type %s: Missing file' % (bookId, type)
					corrupt.append((bookId, type))
				elif not checksumCheck(filepath, md5digest):
					print 'Book %s, Type %s: Digest mismatch: %s' % (bookId, type, md5digest)
					corrupt.append((bookId, type))
	with codecs.open('corrupt.txt', 'w', 'UTF-8') as f:
		for bookId, type in corrupt:
			print >> f, bookId
	return corrupt


def synchBookList(bookList):
	with codecs.open(bookList, 'r', 'UTF-8') as f:
		for line in f.readlines():
			if line.startswith('#'): continue
			bookId = line.strip()
			synchBook(bookId)
			time.sleep(DELAY) # in seconds


def main():
	#synchBookList('corrupt.txt')
	#synchBookList('known_books.txt')
	integrityCheck(TINGPATH)



if __name__ == '__main__':
	main()


