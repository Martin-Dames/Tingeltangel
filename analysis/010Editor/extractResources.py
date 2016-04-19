# -*- coding: utf-8 -*-
#
# Date: 08.04.2016
# Author: Glaukon Ariston
#
import os
import re
from defines import BOOK_REPOSITORY
from misc import timestamp, run010EditorScript


RE_OUF_FILE = re.compile(r'.+\.(ouf)$', re.IGNORECASE)


def processAllBooks(repository):
	oufFiles = []
	for root, dirnames, filenames in os.walk(repository):
		oufFiles.extend(
			fn
			for fn in filenames if RE_OUF_FILE.match(fn)
		)
		break

	print 'extractResources: About to process %s books from %s' % (len(oufFiles), repository)
	for filename in oufFiles:
		run010EditorScript('extractResources', filename)


def main():
	#run010EditorScript('extractResources', '00000_en.ouf')
	#run010EditorScript('extractResources', '00007_en.ouf')
	processAllBooks(BOOK_REPOSITORY)
	print '%s: Done' % (timestamp(),)


if __name__ == '__main__':
	main()
