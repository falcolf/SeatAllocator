import sqlite3

db = sqlite3.connect('students.db')
cursor = db.cursor()

cursor.execute(' select distinct sem from students ')
sems = cursor.fetchall()
print(sems)
for sem in sems:
	cursor.execute('''select distinct ccode from students where sem = ?''',(sem[0],))
	ccodes = cursor.fetchall()
	for code in ccodes:
		cursor.execute('''select distinct facid from students where sem = ? and ccode = ?''',(sem[0],code[0]))
		facs = cursor.fetchall()
		for fac in facs:
			cursor.execute('''select regno from students where facid = ? and sem = ? and ccode = ?''',(fac[0],sem[0],code[0]))
			regs = cursor.fetchall()
			studs = [ reg[0] for reg in regs ]
			elim = [sem[0] , code[0] , fac[0]]
			rec = elim + studs
			rec = ','.join(rec)
			print(rec)
