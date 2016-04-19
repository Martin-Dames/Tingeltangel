#!/usr/bin/python
# -*- coding: utf-8 -*-
#
# Date: 07.04.2016
# Author: Glaukon Ariston
# Abstract:
#    Merge CSV files.
#
#from __future__ import unicode_literals

import os
import codecs
import csv
import re
from misc import timestamp
from defines import STATS_REPOSITORY, BOOK_REPOSITORY


OUF_STATS_CSV = os.path.join(STATS_REPOSITORY, 'knownBooks.csv')
SCRIPT_STATS_CSV = os.path.join(STATS_REPOSITORY, 'knownBooks_scriptStats.csv')
FILE_TYPES = ["Thumb", "File", "Script"]


TINGPATH = BOOK_REPOSITORY
LOCAL_PATH = {}
LOCAL_PATH["Desc"] = TINGPATH + "/%s_en.txt"
LOCAL_PATH["Thumb"] = TINGPATH + "/%s_en.png"
LOCAL_PATH["File"] = TINGPATH + "/%s_en.ouf"
LOCAL_PATH["Script"] = TINGPATH + "/%s_en.src"
BOOK_ATTRIBUTES  = [
	'Name',
	'Publisher',
	'Author',
	'Book Version',
	'URL',
	'ThumbMD5',
	'FileMD5',
	'ScriptMD5',
	'Book Area Code'
]


RE_OPTIONS = re.LOCALE | re.UNICODE | re.DOTALL | re.IGNORECASE | re.MULTILINE
RE_BOOK_DIR = re.compile(r'^\d{5}$', re.IGNORECASE)
RE_SCRIPT_FILE = re.compile(r'.+\.(s)$', re.IGNORECASE)
RE_MD5_ENTRY = re.compile(ur'^(.+)MD5: ([0-9,a-f]+)', re.LOCALE | re.UNICODE | re.IGNORECASE)


def unicode_csv_reader(utf8_data, encoding, xdialect=csv.excel, **kwargs):
	csv_reader = csv.reader(utf8_data, dialect=xdialect, **kwargs)
	for row in csv_reader:
		yield [cell.decode(encoding) for cell in row]


def utf_8_encoder(unicode_csv_data):
    for line in unicode_csv_data:
        yield line.encode('utf-8')


def mergeCsvs(repository, bookIds, what):
	headerRow = None # assume it is the same across the CSV files
	content = []
	for bookId in bookIds:
		csvFile = os.path.join(repository, bookId, '%s%s.csv' % (bookId, what))
		if not os.path.isfile(csvFile):
			continue
		print csvFile
		with open(csvFile) as f:
			reader = unicode_csv_reader(f, 'utf-8', delimiter=';', quotechar='"')
			rownum = 0
			for row in reader:
				# Save header row.
				if rownum == 0:
					if not headerRow:
						headerRow = row
					else:
						if headerRow != row: 
							print headerRow 
							print row
						assert headerRow == row
				else:
					content.append(row)
				rownum += 1
	return (headerRow, content)


'''
Name: Demo-Poster für Didacta
Publisher: Finken
Author: Petra Golisch
Book Version: 1492
URL: 
ThumbMD5: 1795c5a9e6a53adb75bdb4978eb0b4e2
FileMD5: d03c42ca9147f38ccd5fbbf694ab58f3
Book Area Code: en
'''
def readDescFile(bookId):
	filepath = LOCAL_PATH['Desc'] % bookId
	attrs = {}
	with codecs.open(filepath, 'rb', 'utf-8') as f:
		for line in f.readlines():
			line = line.strip()
			if not line: continue
			m = re.match(ur'^(.+):(.*)$', line)
			(key, value) = m.group(1,2)
			attrs[key.strip()] = value.strip()
		return attrs


def mergeStats(repository, bookIds, resultFilepath, what):
	(headerRow, content) = mergeCsvs(repository, bookIds, what)
	
	headerRow += BOOK_ATTRIBUTES
	for row in content:
		bookId = row[0]	# assume the bookId is in the first column
		# load in the data from the description file
		attrs = readDescFile(bookId)
		assert len(attrs.keys()) <= len(BOOK_ATTRIBUTES)
		attrs = [attrs.get(key, '') for key in BOOK_ATTRIBUTES]
		row += attrs
	
	with open(resultFilepath, 'wb') as f:
		w = csv.writer(f, delimiter=';', quotechar='"', quoting=csv.QUOTE_MINIMAL)
		w.writerow(headerRow)
		for row in content:
			w.writerow([c.encode('utf-8') for c in row])
			#w.writerow(row)
	print 'Result saved to %s' % (resultFilepath,)
	return (headerRow, content)


def processBooks(repository):
	bookIds = []
	booksDir = os.path.join(repository, 'books')
	for root, dirnames, filenames in os.walk(booksDir):
		bookIds.extend(
			os.path.relpath(os.path.join(root, dir), booksDir) 
			for dir in dirnames if RE_BOOK_DIR.match(dir)
		)
		break

	print 'mergeCsv: About to process %s books from %s' % (len(bookIds), booksDir)
	mergeStats(booksDir, bookIds, OUF_STATS_CSV, '')
	mergeStats(booksDir, bookIds, SCRIPT_STATS_CSV, '_scriptStats')


def main():
	print '%s # Start' % (timestamp())
	processBooks(STATS_REPOSITORY)
	print '%s # Done' % (timestamp())


if __name__ == "__main__":
	main()
