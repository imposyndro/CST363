
package com.csumb.cst363;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * A pharmacy manager requests a report of the quantity of drugs that have been 
 * used to fill prescriptions by the pharmacy. The report will contain the names of 
 * drugs used and the quantity of each drug used. Input is pharmacy id and a start 
 * and end date range.
 */

public class pharmacyReport {

	static final String DBURL = "jdbc:mysql://localhost:3306/drugstoredb";  // database URL
	static final String USERID = "root";
	static final String PASSWORD = "memorypastry";
	
	public static void main(String[] args) {
		
			try (Connection conn = DriverManager.getConnection(DBURL, USERID, PASSWORD);) {
			
			PreparedStatement ps;
			ResultSet rs;
			Scanner input;
			
			input = new Scanner(System.in);
			
			System.out.println("Please enter the pharmacy ID: ");			
			String pharmacy_id = input.nextLine();
			
			System.out.println("Please enter a date (YYYY-MM-DD) to start searching from: ");			
			String start_date = input.nextLine();
			
			System.out.println("Please enter a date (YYYY-MM-DD) to end searching to: ");			
			String end_date = input.nextLine();
			
			input.close();
			
			// Query to fetch data
			ps = conn.prepareStatement("SELECT d.formula, d.tradeName, SUM(pr.quantity) AS quantity "
					                 + "FROM prescription AS pr "
					                 + "INNER JOIN pharmacy_fills_prescription AS pfp "
					                 + "ON pr.rxNumber = pfp.rxNumber "
					                 + "INNER JOIN drug AS d "
					                 + "ON pr.drug_id = d.drug_id "
					                 + "WHERE pfp.date >= ? AND pfp.date <= ? AND pharmacy_id = ? "
					                 + "GROUP BY d.formula, d.tradeName;");
			ps.setString(1,  start_date);
			ps.setString(2, end_date);
			ps.setString(3,  pharmacy_id);
			rs = ps.executeQuery();
			
			String formulaStr = "Formula";
			String tradeNameStr = "Trade Name";
			String quantityStr = "Quantity";
			
			System.out.println("|=============================================|=============================================|=============================================|");
			System.out.printf("|%-45s|%-45s|%-45s|\n", formulaStr, tradeNameStr, quantityStr);
			System.out.println("|=============================================|=============================================|=============================================|");
			
			while (rs.next()) {
				String formula = rs.getString("formula");
				String tradeName = rs.getString("tradeName");
				String quantity = rs.getString("quantity");
				System.out.printf("|%-45s|%-45s|%-45s|\n", formula, tradeName, quantity);
				System.out.println("|---------------------------------------------|---------------------------------------------|---------------------------------------------|");
			}
		} catch (SQLException e) {
			System.out.println("Error: SQLException "+e.getMessage());
		}
	}
}
