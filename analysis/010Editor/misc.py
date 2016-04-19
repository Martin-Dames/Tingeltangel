# -*- coding: utf-8 -*-
#
# Date: 15.04.2016
# Author: Glaukon Ariston
#
import time, datetime
from focusDefender import defendFocus
from defines import APP_010EDITOR


def timestamp():
	#FORMAT = '%Y-%m-%d %H:%M:%S'
	FORMAT = '%H:%M:%S'
	ts = time.time()
	return datetime.datetime.fromtimestamp(ts).strftime(FORMAT)


def run010EditorScript(script, params):
	print '%s %s' % (timestamp(), params)
	# -noui -nowarnings 
	cmdparams = r'''-noui -script:%s.1sc:(%s) -exit''' % (script, params)
	defendFocus(APP_010EDITOR, cmdparams)


