# -*- coding: utf-8 -*-
#
# Date: 08.04.2016
# Author: Glaukon Ariston
#
import os
import re
from defines import STATS_REPOSITORY
from misc import timestamp, run010EditorScript


RE_BOOK_DIR = re.compile(r'^\d{5}$', re.IGNORECASE)
RE_SCRIPT_FILE = re.compile(r'.+\.(s)$', re.IGNORECASE)


def processAllBooks(repository):
	bookIds = []
	booksDir = os.path.join(repository, 'books')
	for root, dirnames, filenames in os.walk(booksDir):
		bookIds.extend(
			os.path.relpath(os.path.join(root, dir), booksDir) 
			for dir in dirnames if RE_BOOK_DIR.match(dir)
		)
		break

	print 'gatherScriptStats: About to process %s books from %s' % (len(bookIds), booksDir)
	for bookId in bookIds:
		run010EditorScript('gatherScriptStats', bookId)


def main():
	#parseBook('00010')
	processAllBooks(STATS_REPOSITORY)
	print '%s: Done' % (timestamp(),)


if __name__ == '__main__':
	main()
