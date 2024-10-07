-- 1 How many pharmacies carry a specific drug?
SELECT COUNT(pharmacy_id) AS numberOfPharmacies, drug_id
FROM pharmacy_has_drug
WHERE drug_id = 1;

-- 2 How many drugs does a pharmacy carry?
SELECT COUNT(drug_id) AS numberOfDrugs
FROM pharmacy_has_drug
WHERE pharmacy_id = 1;

-- 3 How many patients does a doctor have?
SELECT COUNT(ssn) AS numberOfPatients
FROM patient
WHERE doctor_id = 1; 

-- 4 Which pharmacy carries the most expensive drug?
SELECT pharmacy_id
FROM pharmacy_has_drug
WHERE drugPrice = 
	(SELECT MAX(drugPrice)
    FROM pharmacy_has_drug);
    
-- 5 Which pharmaceutical company makes the most expensive drug?    
SELECT pc.pharmaCompany_id
FROM pharmacompany_makes_drug AS pc
INNER JOIN pharmacy_has_drug AS p
ON pc.drug_id = p.drug_id
WHERE p.drugPrice = 
	(SELECT MAX(drugPrice)
    FROM pharmacy_has_drug);