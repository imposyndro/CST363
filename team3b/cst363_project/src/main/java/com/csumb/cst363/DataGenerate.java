package com.csumb.cst363;


import com.csumb.cst363.Patient;

import javax.management.remote.TargetedNotification;
import java.sql.*;
import java.util.Random;

public class DataGenerate {


    //change credentials to match the correct ones for your system
    static final String DBURL = "jdbc:mysql://localhost:3306/drugstoredb";  // database URL
    static final String USERID = "root";
    static final String PASSWORD = "memorypastry";

    public static void main(String[] args) {
        //grab connection to database
        try(Connection conn = DriverManager.getConnection(DBURL, USERID, PASSWORD);){
            //arrays for doctor and patient objects
            Patient[] patients = new Patient[100];
            Doctor[] doctors = new Doctor[10];
            //prepared statements
            PreparedStatement ps = conn.prepareStatement("INSERT INTO patient VALUES(?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement ps2 = conn.prepareStatement("INSERT INTO doctor VALUES(?,?,?,?,?,?)");

            //loop to generate 10 doctors
            for(int i = 0; i < 10; i++){
                doctors[i] = generateDoctor();
            }

            //loop to generate 100 patients
            for(int i = 0; i < 100; i++){
                patients[i] = generatePatient();
            }



            for(Doctor doctor : doctors){
                System.out.println(doctor);
                ps2.setInt(1, 0);
                ps2.setString(2, doctor.getSsn());
                ps2.setString(3, doctor.getLast_name());
                ps2.setString(4, doctor.getFirst_name());
                ps2.setString(5, doctor.getPractice_since_year());
                ps2.setString(6, doctor.getSpecialty());
                ps2.executeUpdate();
            }

            for(Patient patient : patients){
                System.out.println(patient);
                ps.setInt(1,0);
                ps.setInt(2, patient.getPrimaryID());
                ps.setString(3, patient.getSsn());
                ps.setString(4, patient.getFirst_name());
                ps.setString(5, patient.getLast_name());
                ps.setString(6, patient.getBirthdate());
                ps.setString(7, patient.getStreet());
                ps.setString(8, patient.getCity());
                ps.setString(9, patient.getState());
                ps.setString(10, patient.getZipcode());
                ps.executeUpdate();
            }


        }catch (Exception e){
            System.out.println("ERROR: SQLException " + e.getMessage());
        }

    }

    public static Patient generatePatient(){
        Patient patient = new Patient();
        Random rand = new Random();

        //generates a random patient
        patient.setPatientId(generateString(10));
        patient.setFirst_name(generateString(12));
        patient.setLast_name(generateString(12));
        patient.setBirthdate(generateDate());
        patient.setSsn(generateNum(9));
        patient.setStreet(generateString(10));
        patient.setState(generateString(2));
        patient.setCity(generateString(10));
        patient.setZipcode(generateString(5));
        patient.setPrimaryID(generatePrimaryId());
        patient.setYears(generateString(2));
        patient.setSpecialty(generateString(10));

        return patient;
    }

    //generates a random doctor
    public static Doctor generateDoctor(){
        Doctor doctor = new Doctor();
        Random rand = new Random();

        doctor.setId(rand.nextInt(1000));
        doctor.setLast_name(generateString(10));
        doctor.setFirst_name(generateString(10));
        doctor.setSpecialty(generateSpec());
        doctor.setPractice_since_year(generateYear());
        doctor.setSsn(generateNum(9));

        return doctor;
    }

    //random string generator
    public static String generateString(int len){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(len);

        for(int i = 0; i < len; i++){
            int index = rand.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    //random number generator from 1-9
    public static String generateNum(int len){
        final String chars = "0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(len);

        for(int i = 0; i < len; i++){
            int index = rand.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    //pick random specialty from list
    public static String generateSpec(){

        Random gen = new Random();

        String[] specs = { "Internal Medicine",
                "Family Medicine", "Pediatrics", "Orthopedics",
                "Dermatology",  "Cardiology", "Gynecology",
                "Gastroenterology", "Psychiatry", "Oncology" };

        int index = gen.nextInt(specs.length);
        String random_specialty = specs[index];

        return random_specialty;

    }

    //date generator in the format year-mm-dd
    public static String generateDate(){
        Random rand = new Random();
        int day = rand.nextInt(28) + 1;
        int month = rand.nextInt(12) + 1;
        int year = rand.nextInt(50) + 1972;

        return String.format("%04d-%02d-%02d", year, month, day);
    }
    //year generator
    public static String generateYear(){
        Random rand = new Random();

        int year = rand.nextInt(50) + 1972;

        return String.format("%04d", year);
    }

    public static int generatePrimaryId(){
        Random rand = new Random();

        // Get the number of rows in the doctor table.
        int numRows = 0;
        try (Connection conn = DriverManager.getConnection(DBURL, USERID, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM doctor");) {
            if (rs.next()) {
                numRows = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: SQLException " + e.getMessage());
        }

        // Generate a random primary ID between 1 and the number of rows in the `patient` table.

        return rand.nextInt(numRows) + 1;
    }


}
