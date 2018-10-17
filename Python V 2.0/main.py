import sqlite3
import os
db = sqlite3.connect('students.db')
cursor = db.cursor()
op_file = open('final_order.csv','w')
str_reg = ['Registration_Number_']*30
for i in range(30):
	str_reg[i]=str_reg[i]+str(i+1)
str_reg = ','.join(str_reg)
op_file.write('Semester,Subject_Code,Faculty_ID,'+str_reg+'\n')
cursor.execute(' select distinct sem from students order by sem')
sems = cursor.fetchall()
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
			recs_no = len(studs)
			rows_no = int(recs_no/30)
			for index in range(rows_no):
				btch_stu = studs[index*30:index*30+30]
				frec = elim+btch_stu
				op_file.write(','.join(frec)+'\n')
			if recs_no % 30 > 0:
				btch_stu = studs[rows_no*30:]
				frec = elim+btch_stu
				op_file.write(','.join(frec)+'\n')
op_file.close()
os.system("say 'Document Writing Completed, Parth'")