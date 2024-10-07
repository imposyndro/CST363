package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatient {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Request blank patient registration form.
	 */
	@GetMapping("/patient/new")
	public String newPatient(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new Patient());
		return "patient_register";
	}
	
	/*
	 * Process new patient registration	 */
	@PostMapping("/patient/new")
	public String newPatient(Patient patient, Model model) {

		// TODO
		/*
		 * Complete database logic to verify and process new patient
		 */
		try (Connection con = getConnection()) {
			PreparedStatement ps = con.prepareStatement("insert into patient(last_name, first_name, street, city, state, zip_code, birthdate, ssn, doctor_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			PreparedStatement ps2 = con.prepareStatement("select specialty from doctor where first_name=? AND last_name=?");
			String[] primaryName2 = patient.getPrimaryName().split("\\s+");
			ps2.setString(1, primaryName2[0]);
			ps2.setString(2, primaryName2[1] + " " + primaryName2[2]);
			ResultSet rs2 = ps2.executeQuery();
			if (rs2.next()) {
				patient.setSpecialty(rs2.getString(1));
			}

			Period diff = Period.between(LocalDate.parse(patient.getBirthdate()), LocalDate.now());
			System.out.println("Years between: " + diff.getYears());
			System.out.println("Doctor specialty: " + patient.getSpecialty());
			System.out.println("Patient city: " + patient.getCity());
			System.out.println("Patient DOB: " + patient.getBirthdate());

			if (patient.getSsn().matches("^0{1}[0-9]{8}") || patient.getSsn().matches("^9{1}[0-9]{8}")) {
				model.addAttribute("message", "Please enter a social security number that does not start with 0 or 9.");
				return "patient_register";
			} else if (patient.getSsn().matches("^[0-9]{3}00[0-9]{4}$")) {
				model.addAttribute("message", "Please enter a social security number that does not have '00' for the 4th & 5th digits.");
				return "patient_register";
			} else if (patient.getSsn().matches("^[0-9]{5}[0]{4}")) {
				model.addAttribute("message", "Please enter a social security number that does not have '0000' for the last 4 digits digits.");
				return "patient_register";
			} else if (patient.getSsn().length() != 9) {
				model.addAttribute("message", "Please enter a social security number of 9 digits.");
				return "patient_register";
			} else if (patient.getFirst_name().matches("\s*")) {
				model.addAttribute("message", "Please enter a first name.");
				return "patient_register";
			} else if (! patient.getFirst_name().matches("[a-zA-Z]+")) {
				model.addAttribute("message", "Please only use letters when entering the first name.");
				return "patient_register";
			} else if (patient.getLast_name().matches("\s*")) {
				model.addAttribute("message", "Please enter a last name.");
				return "patient_register";
			} else if (! patient.getLast_name().matches("[a-zA-Z]+")) {
				model.addAttribute("message", "Please only use letters when entering the last name.");
				return "patient_register";
			} else if (LocalDate.parse(patient.getBirthdate()).isBefore(LocalDate.parse("1900-01-01")) || LocalDate.parse(patient.getBirthdate()).isAfter(LocalDate.now())) {
				model.addAttribute("message", "Please enter a birthdate that is after January 1, 1900 and before today's date.");
				return "patient_register";
			} else if (patient.getCity().matches("\s*")) {
				model.addAttribute("message", "Please enter a city.");
				return "patient_register";
			} else if (! patient.getCity().matches("^[a-zA-Z]+( [a-zA-Z]+)*( [a-zA-Z]+)*$")) {
				model.addAttribute("message", "Please only use letters when entering the city.");
				return "patient_register";
			} else if (patient.getState().matches("\s*")) {
				model.addAttribute("message", "Please enter a state.");
				return "patient_register";
			} else if (! patient.getState().matches("[a-zA-Z]+")) {
				model.addAttribute("message", "Please only use letters when entering the state.");
				return "patient_register";
			} else if (patient.getState().length() != 2) {
				model.addAttribute("message", "Please enter the two digit state code.");
				return "patient_register";
			}

			if (! patient.getZipcode().matches("[0-9]+")) {
				model.addAttribute("message", "Please only use numbers when entering the zip code.");
				return "patient_register";
			} else if (patient.getZipcode().length() != 5) {
				if (patient.getZipcode().length() != 9) {
					model.addAttribute("message", "Please enter a zip code of 5 digits or 9 digits.");
					return "patient_register";
				}
			}

			if (diff.getYears() < 18 && ! patient.getSpecialty().equals("Pediatrics")) {
				model.addAttribute("message", "Please select a doctor that specializes in pediatrics for anyone under 18 years old.");
				return "patient_register";
			} else if (! patient.getSpecialty().equals("Internal Medicine")) {
				if (! patient.getSpecialty().equals("Family Medicine")) {
					model.addAttribute("message", "Please select a doctor that specializes in Internal Medicine or Family Medicine.");
					return "patient_register";
				}
			}

			ps.setString(1, patient.getLast_name());
			ps.setString(2, patient.getFirst_name());
			ps.setString(3, patient.getStreet());
			ps.setString(4, patient.getCity());
			ps.setString(5, patient.getState());
			ps.setString(6, patient.getZipcode());
			ps.setString(7, patient.getBirthdate());
			ps.setString(8, patient.getSsn());
			PreparedStatement ps1 = con.prepareStatement("select id from doctor where first_name=? AND last_name=?");
			String[] primaryName = patient.getPrimaryName().split("\\s+");
			ps1.setString(1, primaryName[0]);
			ps1.setString(2, primaryName[1] + " " + primaryName[2]);
			ResultSet rs1 = ps1.executeQuery();
			if (rs1.next()) {
				ps.setInt(9, rs1.getInt(1));
			}

			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				patient.setPatientId((rs.getString(1)));
			}

			// display message and patient information
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("patient", patient);
			return "patient_show";

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", patient);
			return "patient_register";
		}
	}
	
	/*
	 * Request blank form to search for patient by and id
	 */
	@GetMapping("/patient/edit")
	public String getPatientForm(Model model) {
		return "patient_get";
	}
	
	/*
	 * Perform search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String getPatientForm(@RequestParam("patientId") String patientId, @RequestParam("last_name") String last_name,
			Model model) {

		// TODO
		/*
		 * code to search for patient by id and name retrieve patient data and primary
		 * doctor
		 */
		Patient patient = new Patient();
		patient.setPatientId(patientId);
		patient.setLast_name(last_name);

		try (Connection con = getConnection()) {
			// for DEBUG
			System.out.println("start getPatientForm "+ patient);
			PreparedStatement ps = con.prepareStatement("select first_name, last_name, birthdate, street, city, state, zip_code, doctor_id from patient where id=? and last_name=?");
			ps.setString(1, patient.getPatientId());
			ps.setString(2, patient.getLast_name());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				patient.setFirst_name(rs.getString(1));
				patient.setLast_name(rs.getString(2));
				patient.setBirthdate(rs.getString(3));
				patient.setStreet(rs.getString(4));
				patient.setCity(rs.getString(5));
				patient.setState(rs.getString(6));
				patient.setZipcode(rs.getString(7));

				PreparedStatement ps1 = con.prepareStatement("select first_name, last_name from doctor where id=?");
				ps1.setInt(1, rs.getInt(8));
				ResultSet rs1 = ps1.executeQuery();

				if (rs1.next()) {
					patient.setPrimaryName(rs1.getString(1) + " " + rs1.getString(2));
				}
				model.addAttribute("patient", patient);

				// for DEBUG
				System.out.println("end getPatientForm "+ patient);
				return "patient_show";

			} else {
				model.addAttribute("message", "Patient not found.");
				return "patient_get";
			}

		} catch (SQLException e) {
			System.out.println("SQL error in getPatientForm "+ e.getMessage());
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", patient);
			return "patient_get";
		}
	}

	/*
	 *  Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{patientId}")
	public String updatePatient(@PathVariable String patientId, Model model) {

		// TODO Complete database logic search for patient by id.
		try (Connection con = getConnection()) {
			// return fake data.
			Patient patient = new Patient();
			patient.setPatientId(patientId);

			PreparedStatement ps = con.prepareStatement("select first_name, last_name, birthdate, street, city, state, zip_code, doctor_id from patient where id=?");
			ps.setString(1, patient.getPatientId());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				patient.setFirst_name(rs.getString(1));
				patient.setLast_name(rs.getString(2));
				patient.setBirthdate(rs.getString(3));
				patient.setStreet(rs.getString(4));
				patient.setCity(rs.getString(5));
				patient.setState(rs.getString(6));
				patient.setZipcode(rs.getString(7));
				patient.setPrimaryID(rs.getInt(8));

				PreparedStatement ps1 = con.prepareStatement("select first_name, last_name, specialty, practice_since from doctor where id=?");
				ps1.setInt(1, rs.getInt(8));
				ResultSet rs1 = ps1.executeQuery();

				if (rs1.next()) {
					patient.setPrimaryName(rs1.getString(1) + " " + rs1.getString(2));
					patient.setSpecialty(rs1.getString(3));
					patient.setYears(rs1.getString(4));
				}
			}

			model.addAttribute("patient", patient);
			return "patient_edit";
		}catch (SQLException e) {
			System.out.println("SQL error in getPatientForm "+ e.getMessage());
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient ID", patientId);
			return "patient_edit";
		}
	}
	
	
	/*
	 * Process changes to patient profile.  
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(Patient patient, Model model) {

		// TODO
		try (Connection con = getConnection()) {
			PreparedStatement ps = con.prepareStatement("update patient set last_name=?, first_name=?, street=?, city=?, state=?, zip_code=?, birthdate=?, doctor_id=? where id=?",
			Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, patient.getLast_name());
			ps.setString(2, patient.getFirst_name());
			ps.setString(3, patient.getStreet());
			ps.setString(4, patient.getCity());
			ps.setString(5, patient.getState());
			ps.setString(6, patient.getZipcode());
			ps.setString(7, patient.getBirthdate());

			PreparedStatement ps1 = con.prepareStatement("select id from doctor where first_name=? AND last_name=?");
			String[] primaryName = patient.getPrimaryName().split("\\s+");
			ps1.setString(1, primaryName[0]);
			ps1.setString(2, primaryName[1] + " " + primaryName[2]);
			ResultSet rs1 = ps1.executeQuery();
			if (rs1.next()) {
				ps.setInt(8, rs1.getInt(1));
			}

			ps.setString(9, patient.getPatientId());

			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();

			// display message and patient information
			model.addAttribute("message", "Edit successful.");
			model.addAttribute("patient", patient);
			return "patient_show";

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", patient);
			return "patient_edit";
		}
	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

}
