package com.ssi.task8;

import java.sql.*;
import java.util.Scanner;

public class Employee {

	public void showMenu() {

		System.out.println("Select from Given option");
		System.out.println("1. Add Employee");
		System.out.println("2. View All Employee");
		System.out.println("3. Remove Employee");
		System.out.println("4. Clear Data");
		System.out.println("5. Change Salary");
		System.out.println("6. Search Employee");
		System.out.println("7. View Departmentwise List");
		// System.out.println("8. Calculate Gross Salary");
		System.out.println("8. Exit");
		System.out.println("Enter your choise");

	}

	/*
	 * Method to implement switch case take int choice as parameter return nothing
	 */
	public void performOperation(int choice, Scanner sc) throws SQLException {

		switch (choice) {
		case 1:
			this.addEmployee(sc);
			break;
		case 2:
			this.showEmployees();
			break;
		case 3:
			deleteEmployee(sc);
			break;
		case 4:
			System.out.println("rows affected " + clearData());
			break;
		case 5:
			updateSalary(sc);
			break;
		case 6:
			searchEmployee(sc);
			break;
		case 7:
			deptWiseList();
			break;
		case 8:
			System.out.println("----Thank you-----");
			System.exit(0);
			break;
		default:
			System.out.println("----Thank you-----");
			System.exit(0);
			break;
		}
	}

	public Connection connectDB() {
		// driver loading
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wp", "root", "rr@99");
			return con;

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
			System.exit(0);
		}
		return null;

	}

	/*
	 * method to add employee using stored procedure
	 * add_emp_proc(empno,ename,sal,job,dept)
	 */
	public void addEmployee(Scanner sc1) throws SQLException {
		int empno, sal;
		String ename, job, dept;
		System.out.println("Enter employee No");
		empno = sc1.nextInt();
		System.out.println("Enter employee Name");
		ename = sc1.next();
		System.out.println("Enter employee Salary");
		sal = sc1.nextInt();
		System.out.println("Enter employee job");
		job = sc1.next();
		System.out.println("Enter employee department");
		dept = sc1.next();
		Connection con = connectDB();
		String sql = "call add_emp_proc(?,?,?,?,?)";

		CallableStatement addEmpProcedure = con.prepareCall(sql);
		addEmpProcedure.setInt(1, empno);
		addEmpProcedure.setString(2, ename);
		addEmpProcedure.setInt(3, sal);
		addEmpProcedure.setString(4, job);
		addEmpProcedure.setString(5, dept);
		addEmpProcedure.execute();
		con.close();

	}

	/*
	 * method to update salary using sql function sal_Update(empno,sal)
	 */
	public void updateSalary(Scanner sc1) throws SQLException {

		System.out.println("Enter employee no");
		int empno = sc1.nextInt();
		System.out.println("Enter new salary");
		int sal = sc1.nextInt();
		Connection con = connectDB();
		CallableStatement updateSal = con.prepareCall("{? = CALL sal_Update(?,?)}");
		updateSal.setInt(2, sal);
		updateSal.setInt(3, empno);
		updateSal.execute();
		updateSal.registerOutParameter(1, Types.INTEGER);
		System.out.println(" rows updated " + updateSal.getInt(1));
	}

	/*
	 * method to fetch data from database
	 * 
	 */
	public void showEmployees() throws SQLException {
		Connection con = connectDB();
		String sql = "select * from emp";
		Statement stmt = con.createStatement();
		ResultSet empData = stmt.executeQuery(sql);
		
		if (!empData.next()) {
			System.out.println("No record available");
			return;
		}
		System.out.println("  empno\tename\tsal\tjob\tdept");
		System.out.println("|--------------------------------------|");
		empData.beforeFirst();
		while (empData.next()) {
			System.out.print("  " + empData.getString(1) + "\t" + empData.getString(2) + "\t");
			System.out.print(empData.getString(3) + "\t" + empData.getString(4) + "\t");
			System.out.print(empData.getString(5) + "\n");
			System.out.println("|------------------------------------|");
		}

	}

	/*
	 * methood to delete a employee usin procedure
	 */
	
	public void deleteEmployee(Scanner sc1) throws SQLException {

		System.out.println("enter employee no");
		int empno = sc1.nextInt();
		Connection con = connectDB();
		String sql = "call remove_emp(?)";
		CallableStatement removeEmp;
		try {
			removeEmp = con.prepareCall(sql);
			removeEmp.setInt(1, empno);
			removeEmp.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			con.close();

		}
	}

	public int clearData() throws SQLException {

		Connection con = connectDB();
		Statement stmt = con.createStatement();
		int row = stmt.executeUpdate("delete from emp");
		con.close();
		return row;
	}

	public void searchEmployee(Scanner sc1) throws SQLException {

		System.out.println("Enter employee no");
		int searchKey = sc1.nextInt();

		Connection con = connectDB();
		Statement stmt = con.createStatement();
		String sql = "SELECT * FROM emp where empno = " + searchKey;
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next()) {
			System.out.println("RECORD NOT FOUND ");
			return;
		}
		rs.beforeFirst();
		System.out.println("  empno\tename\tsal\tjob\tdept");
		// System.out.println("|--------------------------------------|");
		while (rs.next()) {
			System.out.print("  " + rs.getString(1) + "\t" + rs.getString(2) + "\t");
			System.out.print(rs.getString(3) + "\t" + rs.getString(4) + "\t");
			System.out.print(rs.getString(5) + "\n");
			// System.out.println("|------------------------------------|");
		}

	}

	public void deptWiseList() throws SQLException {
		Connection con = connectDB();
		Statement stmt = con.createStatement();
		String sql = "SELECT dept,empno,ename,job FROM emp ORDER BY dept";
		ResultSet rs = stmt.executeQuery(sql);
		System.out.println("Department\tEmpNo\tName\tJob");
		while (rs.next()) {
			System.out.print(rs.getString(1) + "\t " + rs.getString(2) + "\t ");
			System.out.print(rs.getString(3) + "\t " + rs.getString(4) + "\n");
		}
		con.close();
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int choice = 0;
		Employee obj = new Employee();
		while (true) {
			obj.showMenu();

			choice = sc.nextInt();

			try {
				obj.performOperation(choice, sc);
			} catch (SQLException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		}
		// sc.close();
	}

}
