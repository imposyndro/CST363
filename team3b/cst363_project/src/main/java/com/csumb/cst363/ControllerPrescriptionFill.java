package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ControllerPrescriptionFill {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	/*
	 * Patient requests form to search for prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_fill";
	}


	/*
	 * Process the prescription fill request from a patient.
	 * 1.  Validate that Prescription p contains rxid, pharmacy name and pharmacy address
	 *     and uniquely identify a prescription and a pharmacy.
	 * 2.  update prescription with pharmacyid, name and address.
	 * 3.  update prescription with today's date.
	 * 4.  Display updated prescription
	 * 5.  or if there is an error show the form with an error message.
	 */
	@PostMapping("/prescription/fill")
	public String processFillForm(Prescription p,  Model model) throws SQLException {


		// TODO

		// validate that Prescription p contains rxid, pharmacy name and pharmacy address
		// and uniquely identify a prescription and a pharmacy.


		// update prescription with pharmacyid, name and address.

		String sql = "SELECT * FROM prescription WHERE rxNumber = ?";
		String query = "SELECT * FROM pharmacy WHERE name = ?";
		try (Connection conn = getConnection()) {
			PreparedStatement ps = conn.prepareStatement(sql);
			if (p.getRxid() == null || p.getPharmacyName() == null || p.getPharmacyAddress() == null) {
				model.addAttribute("message", "Please enter all required fields.");
				return "prescription_fill";
			}

			ps.setInt(1, Integer.parseInt(p.getRxid()));
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				p.setQuantity(rs.getInt("quantity"));
			}


			ps = conn.prepareStatement(query);
			ps.setString(1, p.getPharmacyName());
			rs = ps.executeQuery();

			if(rs.next()){
				String addressString = rs.getString("street") +
						", " +
						rs.getString("city") +
						", " +
						rs.getString("state") +
						", " +
						rs.getString("zip_code");
				p.setPharmacyAddress(addressString);
			}
			p.setPharmacyID(rs.getString("id"));
			p.setPharmacyName(rs.getString("name"));
			p.setPharmacyPhone(rs.getString("phoneNum"));






		} catch (SQLException e) {
			e.printStackTrace();
		}

// update prescription with today's date.
		try {
			p.setDateFilled(new Date().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

// display the updated prescription

		model.addAttribute("message", "Prescription has been filled.");
		model.addAttribute("prescription", p);
		return "prescription_show";

	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}


}
