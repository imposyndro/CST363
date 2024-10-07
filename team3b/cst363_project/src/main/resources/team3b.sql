-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema DrugStoreDB
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema DrugStoreDB
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `DrugStoreDB` DEFAULT CHARACTER SET utf8 ;
USE `DrugStoreDB` ;

-- -----------------------------------------------------
-- Table `DrugStoreDB`.`doctor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`doctor` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ssn` VARCHAR(11) NOT NULL,
  CHECK(CHAR_LENGTH(`ssn`) <= 11),
  `last_name` VARCHAR(45) NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `practice_since` YEAR(4) NOT NULL,
  `specialty` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`patient` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `doctor_id` INT UNSIGNED NOT NULL,
  `ssn` VARCHAR(11) NOT NULL,
  CHECK(CHAR_LENGTH(`ssn`) <= 11),
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `birthdate` DATE NOT NULL,
  `street` VARCHAR(45) NULL,
  `city` VARCHAR(45) NULL,
  `state` CHAR(2) NULL,
  CHECK(CHAR_LENGTH(`state`) <= 2),
  `zip_code` VARCHAR(10) NULL,
  CHECK(CHAR_LENGTH(`zip_code`) <= 10),
  PRIMARY KEY (`id`),
  INDEX `fk_patient_doctor1_idx` (`doctor_id` ASC) VISIBLE,
  CONSTRAINT `fk_patient_doctor1`
    FOREIGN KEY (`doctor_id`)
    REFERENCES `DrugStoreDB`.`doctor` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`pharmaCompany`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`pharmaCompany` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `phoneNum` VARCHAR(14) NOT NULL,
  CHECK(CHAR_LENGTH(`phoneNum`) <= 14),
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`pharmacy`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`pharmacy` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `phoneNum` VARCHAR(14) NOT NULL,
  CHECK(CHAR_LENGTH(`phoneNum`) <= 14),
  `name` VARCHAR(45) NOT NULL,
  `street` VARCHAR(45) NOT NULL,
  `city` VARCHAR(45) NOT NULL,
  `state` CHAR(2) NOT NULL,
  CHECK(CHAR_LENGTH(`state`) <= 2),
  `zip_code` VARCHAR(10) NULL,
  CHECK(CHAR_LENGTH(`zip_code`) <= 10),
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`drug`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`drug` (
  `drug_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `tradeName` VARCHAR(45) NULL DEFAULT NULL,
  `formula` VARCHAR(45) NOT NULL,
  UNIQUE INDEX `tradeName_UNIQUE` (`tradeName` ASC) VISIBLE,
  PRIMARY KEY (`drug_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`pharmacy_has_drug`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`pharmacy_has_drug` (
  `pharmacy_id` INT UNSIGNED NOT NULL,
  `drug_id` INT UNSIGNED NOT NULL,
  `drugPrice` DECIMAL(6,2) UNSIGNED NOT NULL,
  PRIMARY KEY (`pharmacy_id`, `drug_id`),
  INDEX `fk_pharmacy_has_drug_drug1_idx` (`drug_id` ASC) VISIBLE,
  INDEX `fk_pharmacy_has_drug_pharmacy1_idx` (`pharmacy_id` ASC) VISIBLE,
  CONSTRAINT `fk_pharmacy_has_drug_drug1`
    FOREIGN KEY (`drug_id`)
    REFERENCES `DrugStoreDB`.`drug` (`drug_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pharmacy_has_drug_pharmacy1`
    FOREIGN KEY (`pharmacy_id`)
    REFERENCES `DrugStoreDB`.`pharmacy` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`prescription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`prescription` (
  `rxNumber` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` INT UNSIGNED NOT NULL,
  `doctor_id` INT UNSIGNED NOT NULL,
  `drug_id` INT UNSIGNED NOT NULL,
  `quantity` INT UNSIGNED NOT NULL,
  `date` DATE NOT NULL,
  PRIMARY KEY (`rxNumber`),
  INDEX `fk_prescription_doctor1_idx` (`doctor_id` ASC) VISIBLE,
  INDEX `fk_prescription_patient1_idx` (`patient_id` ASC) VISIBLE,
  INDEX `fk_prescription_drug1_idx` (`drug_id` ASC) VISIBLE,
  CONSTRAINT `fk_prescription_doctor1`
    FOREIGN KEY (`doctor_id`)
    REFERENCES `DrugStoreDB`.`doctor` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_prescription_patient1`
    FOREIGN KEY (`patient_id`)
    REFERENCES `DrugStoreDB`.`patient` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_prescription_drug1`
    FOREIGN KEY (`drug_id`)
    REFERENCES `DrugStoreDB`.`drug` (`drug_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`pharmaCompany_makes_drug`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`pharmaCompany_makes_drug` (
  `pharmaCompany_id` INT UNSIGNED NOT NULL,
  `drug_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`pharmaCompany_id`, `drug_id`),
  INDEX `fk_pharmaCompany_makes_drug_pharmaCompany1_idx` (`pharmaCompany_id` ASC) VISIBLE,
  INDEX `fk_pharmaCompany_makes_drug_drug1_idx` (`drug_id` ASC) VISIBLE,
  CONSTRAINT `fk_pharmaCompany_makes_drug_pharmaCompany1`
    FOREIGN KEY (`pharmaCompany_id`)
    REFERENCES `DrugStoreDB`.`pharmaCompany` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pharmaCompany_makes_drug_drug1`
    FOREIGN KEY (`drug_id`)
    REFERENCES `DrugStoreDB`.`drug` (`drug_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`pharmacy_fills_prescription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`pharmacy_fills_prescription` (
  `rxNumber` INT UNSIGNED NOT NULL,
  `pharmacy_id` INT UNSIGNED NOT NULL,
  `pharmaCompany_id` INT UNSIGNED NOT NULL,
  `date` DATE NOT NULL,
  `is_generic_name` TINYINT NOT NULL,
  PRIMARY KEY (`rxNumber`, `pharmacy_id`),
  INDEX `fk_pharmacy_fills_prescription_prescription1_idx` (`rxNumber` ASC) VISIBLE,
  INDEX `fk_pharmacy_fills_prescription_pharmacy1_idx` (`pharmacy_id` ASC) VISIBLE,
  INDEX `fk_pharmacy_fills_prescription_pharmaCompany1_idx` (`pharmaCompany_id` ASC) VISIBLE,
  CONSTRAINT `fk_pharmacy_fills_prescription_prescription1`
    FOREIGN KEY (`rxNumber`)
    REFERENCES `DrugStoreDB`.`prescription` (`rxNumber`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pharmacy_fills_prescription_pharmacy1`
    FOREIGN KEY (`pharmacy_id`)
    REFERENCES `DrugStoreDB`.`pharmacy` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pharmacy_fills_prescription_pharmaCompany1`
    FOREIGN KEY (`pharmaCompany_id`)
    REFERENCES `DrugStoreDB`.`pharmaCompany` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DrugStoreDB`.`contract_pharmacy_pharmaCompany`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DrugStoreDB`.`contract_pharmacy_pharmaCompany` (
  `contractId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `pharmacy_id` INT UNSIGNED NOT NULL,
  `pharmaCompany_id` INT UNSIGNED NOT NULL,
  `supervisor_firstName` VARCHAR(45) NOT NULL,
  `supervisor_lastName` VARCHAR(45) NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  CHECK (`start_date` < `end_date`),
  `contract_text` TEXT NULL,
  PRIMARY KEY (`contractId`),
  INDEX `fk_contract_pharmacy_pharmaCompany_pharmacy1_idx` (`pharmacy_id` ASC) VISIBLE,
  INDEX `fk_contract_pharmacy_pharmaCompany_pharmaCompany1_idx` (`pharmaCompany_id` ASC) VISIBLE,
  CONSTRAINT `fk_contract_pharmacy_pharmaCompany_pharmacy1`
    FOREIGN KEY (`pharmacy_id`)
    REFERENCES `DrugStoreDB`.`pharmacy` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_pharmacy_pharmaCompany_pharmaCompany1`
    FOREIGN KEY (`pharmaCompany_id`)
    REFERENCES `DrugStoreDB`.`pharmaCompany` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
