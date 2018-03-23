use wrapper;

UPDATE `wrapper`.`terms_conditions_history` SET `terms_conditions_id`='0' WHERE `id`='2';
UPDATE `wrapper`.`terms_conditions_history` SET `terms_conditions_id`='0' WHERE `id`='1';

use huddil;

ALTER TABLE `huddil`.`facility` 
ADD COLUMN `alternateContactNo` VARCHAR(45) NOT NULL AFTER `contactNo`,
ADD COLUMN `alternateEmailId` VARCHAR(45) NOT NULL AFTER `emailId`;

ALTER TABLE `huddil`.`booking_history` 
ADD COLUMN `seats` INT NOT NULL AFTER `paymentId`;

INSERT INTO `huddil`.`status` (`id`, `name`) VALUES ('-1', 'Saved');
INSERT INTO `huddil`.`status` (`id`, `name`) VALUES ('-2', 'Save With Request');

--
-- Table structure for table `facility_terms_conditions`
--

DROP TABLE IF EXISTS `facility_terms_conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facility_terms_conditions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(400) NOT NULL,
  `loacationId` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `createdDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `facility_terms_locationId_idx` (`loacationId`),
  CONSTRAINT `facility_terms_locationId` FOREIGN KEY (`loacationId`) REFERENCES `location` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!50003 DROP PROCEDURE IF EXISTS `booking` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `booking`(IN v_sessionId VARCHAR(128), IN v_cityId INT, IN v_localityId INT, IN v_month INT, IN v_status INT, IN v_type INT, IN v_pageNo INT, INOUT v_count INT)
BEGIN

	DECLARE v_userType INT;
    DECLARE v_userId INT;
    DECLARE LowerBound INT;
    DECLARE v_totalRecords INT;
    
    SET v_totalRecords = 0;

	SET LowerBound = (v_pageNo - 1) * v_count;


	SELECT p.userType, p.userId INTO v_userType, v_userId FROM huddil.user_pref p WHERE p.sessionId = v_sessionId;
    IF(v_userId IS NULL)THEN
		SET v_count = -1;
	ELSEIF(v_userType != 6 AND v_userType !=7)THEN
        SET v_count = -2;
	ELSE	
		SET @query = "SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo, b.bookedTime, b.totalPrice, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, b.status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats FROM huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryOne= "SELECT COUNT(DISTINCT b.id) INTO @v_totalRecords FROM huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		
		IF(v_userType = 7)THEN
			SET @query = CONCAT(@query,' JOIN huddil.user_pref p ON p.userId = b.userId AND f.spUserId = v_userId');
			SET @queryOne = CONCAT(@queryOne,' JOIN huddil.user_pref p ON p.userId = b.userId AND f.spUserId = v_userId');
		ELSEIF(v_userType = 6)THEN
			SET @query = CONCAT(@query,' JOIN huddil.user_pref p ON p.userId = b.userId');
			SET @queryOne = CONCAT(@queryOne,' JOIN huddil.user_pref p ON p.userId = b.userId');

		END IF;
        
        
		SET @query = CONCAT(@query,' WHERE month(b.fromTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId AND b.status = v_status order by b.bookedTime desc LIMIT LowerBound, v_count');
		SET @queryOne = CONCAT(@queryOne,' WHERE month(b.fromTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId AND b.status = v_status');

		IF(v_month = 0 && v_status = 0 && v_type = 0 && v_cityId =0 && v_localityId = 0)THEN
			SET@query = REPLACE(@query, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId  AND b.status = v_status','');
			SET@queryOne = REPLACE(@queryOne, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId  AND b.status = v_status','');
		END IF;
		IF(v_cityId = 0)THEN
			SET @query = REPLACE(@query, ' AND c.id = v_cityId','');
			SET @queryOne = REPLACE(@queryOne, ' AND c.id = v_cityId','');
		END IF;
		IF(v_localityId = 0)THEN
			SET @query = REPLACE(@query, ' AND l.id = v_localityId','');
			SET @queryOne = REPLACE(@queryOne, ' AND l.id = v_localityId','');
		END IF;
		IF(v_status = 0)THEN
			SET @query = REPLACE(@query, ' AND b.status = v_status',' AND (b.status = 1 OR b.status = 2 OR b.status = 3)');
			SET @queryOne = REPLACE(@queryOne, ' AND b.status = v_status',' AND (b.status = 1 OR b.status = 2 OR b.status = 3)');
		END IF;
		IF(v_type = 0)THEN
			SET @query = REPLACE(@query, ' AND t.id = v_type','');
			SET @queryOne = REPLACE(@queryOne, ' AND t.id = v_type','');
		END IF;
		IF(v_month =0)THEN
			/*SET v_month = MONTH(now());
			SET @query = REPLACE(@query, "v_month", v_month);
			SET @queryOne = REPLACE(@queryOne, "v_month", v_month);*/
            SET @query = REPLACE(@query, 'month(b.fromTime) = v_month AND ', '');
			SET @queryOne = REPLACE(@queryOne, 'month(b.fromTime) = v_month AND ', '');
		ELSE
			SET @query = REPLACE(@query, "v_month", v_month);
			SET @queryOne = REPLACE(@queryOne, "v_month", v_month);
		END IF;
		
        IF(v_status = 5)THEN
			
            IF(v_month = 0 && v_type = 0 && v_cityId =0 && v_localityId = 0)THEN
				SET @query = REPLACE(@query, ' WHERE','');
                SET @query = REPLACE(@query, 'b.status = v_status', '');
                SET @queryOne = REPLACE(@queryOne, ' WHERE','');
                SET @queryOne = REPLACE(@queryOne, 'b.status = v_status', '');
			ELSE
				SET @query = REPLACE(@query, ' AND b.status = v_status', '');
                SET @queryOne = REPLACE(@queryOne, ' AND b.status = v_status', '');
			END IF;
            SET @query = REPLACE(@query, 'SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo', 'SELECT DISTINCT b.bookingId, b.fromDateTime as bookedFrom, b.toDateTime as bookedTo');
            SET @query = REPLACE(@query, 'huddil.booking', 'huddil.booking_history');
			SET @query = REPLACE(@query, 'b.totalPrice', 'b.price as totalPrice');
            SET @query = REPLACE(@query, ', b.status', ',5 as status');
            SET @query = REPLACE(@query, 'b.fromTime', 'b.bookedTime');
            SET @queryOne = REPLACE(@queryOne, 'huddil.booking', 'huddil.booking_history');
			SET @queryOne = REPLACE(@queryOne, 'b.totalPrice', 'b.price');
            SET @queryOne = REPLACE(@queryOne, ', b.status', '');
            SET @queryOne = REPLACE(@queryOne, 'b.fromTime', 'b.bookedTime');            
        END IF;


		SET @query = REPLACE(@query, "v_cityId", v_cityId);
		SET @query = REPLACE(@query, "v_localityId", v_localityId);
		SET @query = REPLACE(@query, "v_type", v_type );
		SET @query = REPLACE(@query, "v_status", v_status);
		SET @query = REPLACE(@query, "LowerBound", LowerBound);
		SET @query = REPLACE(@query, "v_count", v_count);
		SET @query = REPLACE(@query, "v_userId", v_userId);
		
		SET @queryOne = REPLACE(@queryOne, "v_cityId", v_cityId);
		SET @queryOne = REPLACE(@queryOne, "v_localityId", v_localityId);
		SET @queryOne = REPLACE(@queryOne, "v_type", v_type );
		SET @queryOne = REPLACE(@queryOne, "v_status", v_status);
		SET @queryOne = REPLACE(@queryOne, "v_userId", v_userId);

		PREPARE stmt FROM @query;
		EXECUTE stmt;
		PREPARE stmt FROM @queryOne;
		
		EXECUTE stmt;
		
		
		SET v_count = @v_totalRecords;
	END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `bookingsPagination` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `bookingsPagination`(IN v_sessionId VARCHAR(100), IN v_operation INT, IN v_search VARCHAR(100), IN v_pageNo INT, INOUT v_count INT)
BEGIN


/*v_operation = 1 -> get booking by consumer*/
/*v_operation = 2 -> get bookinghistory by consumer*/
/*v_operation = 3 -> get bookingcancellation by consumer*/
/*v_operation = 4 -> get booking based on emailid search by advisor*/

DECLARE v_lowerBound INT;

SET v_lowerBound = (v_pageNo - 1) * v_count;

    IF(v_operation = 1)THEN
	
        SELECT b.id as bookingId, b.bookedTime, b.facilityId as facilityId, CAST(0 AS SIGNED) AS bookingId, 
        IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, f.title, f.typeName as typeName, f.cityName, 
        f.localityName, lo.name as locationName, lo.address, lo.landmark, b.fromTime as bookedFrom, b.toTime as bookedTo, b.totalPrice, 
        CONCAT(UCASE(LEFT(s.name,1)),LCASE(SUBSTRING(s.name,2))) as status, b.seats, k.displayName, k.mobileNo, k.emailId FROM huddil.booking b 
            JOIN huddil.facility f ON b.facilityId = f.id 
            JOIN huddil.location lo ON f.locationId = lo.id 
            JOIN huddil.user_pref p ON b.userId = p.userId
            JOIN huddil.user_pref k on f.spUserId = k.userId
            JOIN huddil.booking_status s ON b.status = s.id 
                WHERE p.sessionId = v_sessionId AND (b.status = 1 OR b.status = 3 OR b.status =5) order by b.id desc LIMIT v_lowerBound, v_count;

        IF(v_pageNo = 1) THEN
            SELECT COUNT(DISTINCT b.id) INTO v_count FROM huddil.booking b 
                JOIN huddil.facility f ON b.facilityId = f.id 
                JOIN huddil.location lo ON f.locationId = lo.id 
                JOIN huddil.user_pref p ON b.userId = p.userId 
                JOIN huddil.booking_status s ON b.status = s.id 
                    WHERE p.sessionId = v_sessionId AND (b.status = 1 OR b.status = 3 OR b.status =5);
		ELSE
			SET v_count =0;
        END IF;
    ELSEIF(v_operation = 2)THEN
    
       
        SELECT b.bookingId, b.bookedTime, b.facilityId as facilityId, 
        IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, f.title, f.typeName as typeName, f.cityName, 
        f.localityName, lo.name as locationName, lo.address, lo.landmark, b.fromDateTime as bookedFrom, b.toDateTime as bookedTo, 
        b.price as totalPrice, 'Completed' as status, b.seats, k.displayName, k.mobileNo, k.emailId  FROM huddil.booking_history b 
            JOIN huddil.facility f ON b.facilityId = f.id 
            JOIN huddil.location lo ON f.locationId = lo.id 
            JOIN huddil.user_pref p ON b.userId = p.userId
            JOIN huddil.user_pref k ON f.spUserId = k.userId
                WHERE p.sessionId = v_sessionId order by b.bookedTime desc LIMIT v_lowerBound, v_count;
        
        IF(v_pageNo = 1)THEN
        
            SELECT COUNT(DISTINCT b.bookingId) INTO v_count FROM huddil.booking_history b 
                JOIN huddil.facility f ON b.facilityId = f.id 
                JOIN huddil.location lo ON f.locationId = lo.id 
                JOIN huddil.user_pref p ON b.userId = p.userId 
                    WHERE p.sessionId = v_sessionId;
		ELSE
			SET v_count =0;
        END IF;
	
	ELSEIF(v_operation =3)THEN
        
        SELECT b.bookingId, b.bookedTime, b.facilityId as facilityId, 
        IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, f.title, f.typeName as typeName, f.cityName, 
        f.localityName, lo.name as name, lo.address, lo.landmark, b.bookedFrom, b.bookedTo, 
        b.totalPrice, b.refundAmount, IF(b.refundId != 'null', 'Refund', 'No Refund') as cancelledStatus,  IF(b.refundId != 'null', b.refundId, null) as refundId, b.seats, p.displayName, p.mobileNo, p.emailId, b.cancelledDateTime as cancelledDate, IF(b.bookedStatus = 4, 'Denied', 'Cancelled') as status FROM huddil.cancellation b 
            JOIN huddil.facility f ON b.facilityId = f.id 
            JOIN huddil.location lo ON f.locationId = lo.id 
            JOIN huddil.user_pref p ON b.bookedUserId = p.userId 
                WHERE p.sessionId = v_sessionId order by b.id desc LIMIT v_lowerBound, v_count;
          
        IF(v_pageNo = 1)THEN
            SELECT COUNT(DISTINCT b.bookingId) INTO v_count FROM huddil.cancellation b 
                JOIN huddil.facility f ON b.facilityId = f.id 
                JOIN huddil.location lo ON f.locationId = lo.id 
                JOIN huddil.user_pref p ON b.bookedUserId = p.userId 
                    WHERE p.sessionId = v_sessionId;
		ELSE
			SET v_count =0;
        END IF;
   
    ELSEIF(v_operation =5)THEN
    
		SELECT b.bookingId, b.bookedTime, b.facilityId as facilityId,
		IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMode, f.title, f.typeName as typeName, f.cityName,
		f.localityName, lo.name as locationName, lo.address, lo.landmark, b.bookedFrom as fromTime, b.bookedTo as toTime,
		b.totalPrice, b.refundAmount, IF(b.refundId != 'null', 'Refund', 'No Refund') as status FROM huddil.cancellation b 
			JOIN huddil.facility f ON b.facilityId = f.id
			JOIN huddil.location lo ON f.locationId = lo.id
			JOIN huddil.user_pref p ON f.spUserId = p.userId 
				WHERE p.sessionId = v_sessionId LIMIT v_lowerBound, v_count;
    
		IF(v_pageNo = 1)THEN
			SELECT COUNT(DISTINCT b.bookingId) INTO v_count FROM huddil.cancellation b 
				JOIN huddil.facility f ON b.facilityId = f.id 
				JOIN huddil.location lo ON f.locationId = lo.id 
				JOIN huddil.user_pref p ON f.spUserId = p.userId 
					WHERE p.sessionId = v_sessionId;
		ELSE
			SET v_count =0;
		END IF;


    ELSEIF(v_operation =4)THEN
		SET @query = "SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, b.status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats 
					FROM huddil.booking b 
						JOIN huddil.facility f ON f.id = b.facilityId 
						JOIN huddil.location lo ON lo.id = f.locationId 
						JOIN huddil.user_pref p ON b.userId = p.userId"; 
		SET @query = CONCAT(@query, ' WHERE b.status > 0 AND p.emailId LIKE ''%',v_search,'%'' order by b.bookedTime desc LIMIT v_lowerBound, v_count');
        SET @query = REPLACE(@query, 'v_lowerBound', v_lowerBound);
		SET @query = REPLACE(@query, 'v_count', v_count);
    
		PREPARE stmt FROM @query;
		EXECUTE stmt;
	
		IF(v_pageNo = 1)THEN
			SET @queryOne = "SELECT DISTINCT COUNT(b.id) INTO @v_count FROM huddil.booking b 
								JOIN huddil.facility f ON f.id = b.facilityId 
								JOIN huddil.location lo ON lo.id = f.locationId 
								JOIN huddil.user_pref p ON b.userId = p.userId"; 
			SET @queryOne = CONCAT(@queryOne, ' WHERE b.status > 0 AND p.emailId LIKE ''%',v_search,'%''');
		
			PREPARE  stmt FROM @queryOne;
			EXECUTE stmt;
			SET v_count = @v_count;
        ELSE
			SET v_count =0;
		END IF;
    
    END IF;
   
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `cancellationDetails` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `cancellationDetails`(IN v_sessionId VARCHAR(128), IN v_cityId INT, IN v_localityId INT, IN v_month INT, IN v_status INT, IN v_type INT, IN v_pageNo INT, INOUT v_count INT)
BEGIN

	DECLARE v_userType INT;
    DECLARE v_userId INT;
    DECLARE LowerBound INT;
    DECLARE v_totalRecords INT;
    
    SET v_totalRecords = 0;

	SET LowerBound = (v_pageNo - 1) * v_count;


	SELECT p.userType, p.userId INTO v_userType, v_userId FROM huddil.user_pref p WHERE p.sessionId = v_sessionId;
    IF(v_userId IS NULL)THEN
		SET v_count = -1;
	ELSEIF(v_userType != 6 AND v_userType !=7)THEN
        SET v_count = -2;
	ELSE	
     
		SET @query = "SELECT DISTINCT b.bookingId,b.bookedFrom, b.bookedTo, b.bookedTime, b.totalPrice, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, IF(bookedStatus = 4, 4, 2) as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats FROM huddil.cancellation b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryOne= "SELECT COUNT(DISTINCT b.bookingId) INTO @v_totalRecords FROM huddil.cancellation b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		
		IF(v_userType = 7)THEN
			SET @query = CONCAT(@query,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId AND f.spUserId = v_userId');
			SET @queryOne = CONCAT(@queryOne,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId AND f.spUserId = v_userId');
		ELSEIF(v_userType = 6)THEN
			SET @query = CONCAT(@query,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId');
			SET @queryOne = CONCAT(@queryOne,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId');

		END IF;

		SET @query = CONCAT(@query,' WHERE month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId AND b.bookedStatus = v_status order by b.bookingId desc LIMIT LowerBound, v_count');
		SET @queryOne = CONCAT(@queryOne,' WHERE month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId AND b.bookedStatus = v_status');

		IF(v_month = 0 && v_status = 0 && v_type = 0 && v_cityId =0 && v_localityId = 0)THEN
			SET@query = REPLACE(@query, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId  AND b.bookedStatus = v_status','');
			SET@queryOne = REPLACE(@queryOne, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId  AND b.bookedStatus = v_status','');
		END IF;
		IF(v_cityId = 0)THEN
			SET @query = REPLACE(@query, ' AND c.id = v_cityId','');
			SET @queryOne = REPLACE(@queryOne, ' AND c.id = v_cityId','');
		END IF;
		IF(v_localityId = 0)THEN
			SET @query = REPLACE(@query, ' AND l.id = v_localityId','');
			SET @queryOne = REPLACE(@queryOne, ' AND l.id = v_localityId','');
		END IF;
		IF(v_status = 2)THEN
			SET @query = REPLACE(@query, ' AND b.bookedStatus = v_status',' AND (b.bookedStatus = 1 OR b.bookedStatus = 2 OR b.bookedStatus = 3)');
			SET @queryOne = REPLACE(@queryOne, ' AND b.bookedStatus = v_status',' AND (b.bookedStatus = 1 OR b.bookedStatus = 2 OR b.bookedStatus = 3)');
		ELSEIF(v_status = 4)THEN
			SET @query = REPLACE(@query, 'v_status', v_status);
			SET @queryOne = REPLACE(@queryOne, 'v_status', v_status);
		END IF;
		IF(v_type = 0)THEN
			SET @query = REPLACE(@query, ' AND t.id = v_type','');
			SET @queryOne = REPLACE(@queryOne, ' AND t.id = v_type','');
		END IF;
		IF(v_month =0)THEN
			/*SET v_month = MONTH(now());
			SET @query = REPLACE(@query, "v_month", v_month);
			SET @queryOne = REPLACE(@queryOne, "v_month", v_month);*/
            SET @query = REPLACE(@query, 'month(b.bookedTime) = v_month AND ', '');
			SET @queryOne = REPLACE(@queryOne, 'month(b.bookedTime) = v_month AND ', '');
		ELSE
			SET @query = REPLACE(@query, "v_month", v_month);
			SET @queryOne = REPLACE(@queryOne, "v_month", v_month);
		END IF;
		
		SET @query = REPLACE(@query, "v_cityId", v_cityId);
		SET @query = REPLACE(@query, "v_localityId", v_localityId);
		SET @query = REPLACE(@query, "v_type", v_type );
		SET @query = REPLACE(@query, "v_status", v_status);
		SET @query = REPLACE(@query, "LowerBound", LowerBound);
		SET @query = REPLACE(@query, "v_count", v_count);
		SET @query = REPLACE(@query, "v_userId", v_userId);
		
		SET @queryOne = REPLACE(@queryOne, "v_cityId", v_cityId);
		SET @queryOne = REPLACE(@queryOne, "v_localityId", v_localityId);
		SET @queryOne = REPLACE(@queryOne, "v_type", v_type );
		SET @queryOne = REPLACE(@queryOne, "v_status", v_status);
		SET @queryOne = REPLACE(@queryOne, "v_userId", v_userId);

		PREPARE stmt FROM @query;
		EXECUTE stmt;
		PREPARE stmt FROM @queryOne;
		
		EXECUTE stmt;
		
		
		SET v_count = @v_totalRecords;
	END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getBookingAndCancellation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getBookingAndCancellation`(IN v_sessionId VARCHAR(128), IN v_cityId INT, IN v_localityId INT, IN v_month INT, IN v_type INT, IN v_pageNo INT, INOUT v_count INT)
BEGIN

	DECLARE v_userType INT;
    DECLARE v_userId INT;
    DECLARE LowerBound INT;
    DECLARE v_totalRecords INT;
    
    SET v_totalRecords = 0;

	SET LowerBound = (v_pageNo - 1) * v_count;


	SELECT p.userType, p.userId INTO v_userType, v_userId FROM huddil.user_pref p WHERE p.sessionId = v_sessionId;
    IF(v_userId IS NULL)THEN
		SET v_count = -1;
	ELSEIF(v_userType != 6 AND v_userType !=7)THEN
        SET v_count = -2;
	ELSE
   		SET @queryBooking = "SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo, b.bookedTime, b.totalPrice, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, b.status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats FROM huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryBookingCount = "SELECT COUNT(DISTINCT b.id) AS count FROM huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryCancellation = "SELECT DISTINCT b.bookingId, b.bookedFrom, b.bookedTo, b.bookedTime, b.totalPrice, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, IF(b.bookedStatus = 4, 4, 2) as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats FROM huddil.cancellation b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryCancellationCount = "SELECT COUNT(DISTINCT b.bookingId) AS count FROM huddil.cancellation b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryCompleted = "SELECT DISTINCT b.bookingId AS id, b.fromDateTime as bookedFrom, b.toDateTime as bookedTo, b.bookedTime, b.price, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, 5 AS status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats FROM huddil.booking_history b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
		SET @queryCompletedCount = "SELECT COUNT(DISTINCT b.bookingId) AS count FROM huddil.booking_history b JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName";
        
		IF(v_userType = 7)THEN
			SET @queryBooking = CONCAT(@queryBooking,' JOIN huddil.user_pref p ON p.userId = b.userId AND f.spUserId = v_userId WHERE 1 = 1');
			SET @queryBookingCount = CONCAT(@queryBookingCount,' JOIN huddil.user_pref p ON p.userId = b.userId AND f.spUserId = v_userId WHERE 1 = 1');
			SET @queryCancellation = CONCAT(@queryCancellation,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId AND f.spUserId = v_userId WHERE 1 = 1');
			SET @queryCancellationCount = CONCAT(@queryCancellationCount,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId AND f.spUserId = v_userId WHERE 1 = 1');
			SET @queryCompleted = CONCAT(@queryCompleted,' JOIN huddil.user_pref p ON p.userId = b.userId AND f.spUserId = v_userId WHERE 1 = 1');
			SET @queryCompletedCount = CONCAT(@queryCompletedCount,' JOIN huddil.user_pref p ON p.userId = b.userId AND f.spUserId = v_userId WHERE 1 = 1');
        ELSEIF(v_userType = 6)THEN
			SET @queryBooking = CONCAT(@queryBooking,' JOIN huddil.user_pref p ON p.userId = b.userId WHERE 1 = 1');
			SET @queryBookingCount = CONCAT(@queryBookingCount,' JOIN huddil.user_pref p ON p.userId = b.userId WHERE 1 = 1');
			SET @queryCancellation = CONCAT(@queryCancellation,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId WHERE 1 = 1');
			SET @queryCancellationCount = CONCAT(@queryCancellationCount,' JOIN huddil.user_pref p ON p.userId = b.bookedUserId WHERE 1 = 1');
			SET @queryCompleted = CONCAT(@queryCompleted,' JOIN huddil.user_pref p ON p.userId = b.userId WHERE 1 = 1');
			SET @queryCompletedCount = CONCAT(@queryCompletedCount,' JOIN huddil.user_pref p ON p.userId = b.userId WHERE 1 = 1');
		END IF;
        
		SET @queryBooking = CONCAT(@queryBooking,' AND month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId AND b.status != 0');
		SET @queryBookingCount = CONCAT(@queryBookingCount,' AND month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId AND b.status !=0');
		SET @queryCancellation = CONCAT(@queryCancellation,' AND month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId');
		SET @queryCancellationCount = CONCAT(@queryCancellationCount,' AND month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId');
		SET @queryCompleted = CONCAT(@queryCompleted,' AND month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId');
		SET @queryCompletedCount = CONCAT(@queryCompletedCount,' AND month(b.bookedTime) = v_month AND t.id = v_type AND c.id = v_cityId AND l.id = v_localityId');

		IF(v_month = 0 && v_type = 0 && v_cityId =0 && v_localityId = 0)THEN
			SET @queryBooking = REPLACE(@queryBooking, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId','');
			SET @queryBookingCount = REPLACE(@queryBookingCount, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId','');
            SET @queryCancellation = REPLACE(@queryCancellation, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId','');
			SET @queryCancellationCount = REPLACE(@queryCancellationCount, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId','');
			SET @queryCompleted = REPLACE(@queryCompleted, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId','');
			SET @queryCompletedCount = REPLACE(@queryCompletedCount, 'AND t.id= v_type AND c.id = v_cityId AND  l.id = v_localityId','');
		END IF;
		IF(v_cityId = 0)THEN
			SET @queryBooking = REPLACE(@queryBooking, ' AND c.id = v_cityId','');
			SET @queryBookingCount = REPLACE(@queryBookingCount, ' AND c.id = v_cityId','');
			SET @queryCancellation = REPLACE(@queryCancellation, ' AND c.id = v_cityId','');
			SET @queryCancellationCount = REPLACE(@queryCancellationCount, ' AND c.id = v_cityId','');
			SET @queryCompleted = REPLACE(@queryCompleted, ' AND c.id = v_cityId','');
			SET @queryCompletedCount = REPLACE(@queryCompletedCount, ' AND c.id = v_cityId','');
		END IF;
		IF(v_localityId = 0)THEN
			SET @queryBooking = REPLACE(@queryBooking, ' AND l.id = v_localityId','');
			SET @queryBookingCount = REPLACE(@queryBookingCount, ' AND l.id = v_localityId','');
			SET @queryCancellation = REPLACE(@queryCancellation, ' AND l.id = v_localityId','');
			SET @queryCancellationCount = REPLACE(@queryCancellationCount, ' AND l.id = v_localityId','');
			SET @queryCompleted = REPLACE(@queryCompleted, ' AND l.id = v_localityId','');
			SET @queryCompletedCount = REPLACE(@queryCompletedCount, ' AND l.id = v_localityId','');
		END IF;
		IF(v_type = 0)THEN
			SET @queryBooking = REPLACE(@queryBooking, ' AND t.id = v_type','');
			SET @queryBookingCount = REPLACE(@queryBookingCount, ' AND t.id = v_type','');
			SET @queryCancellation = REPLACE(@queryCancellation, ' AND t.id = v_type','');
			SET @queryCancellationCount = REPLACE(@queryCancellationCount, ' AND t.id = v_type','');
			SET @queryCompleted = REPLACE(@queryCompleted, ' AND t.id = v_type','');
			SET @queryCompletedCount = REPLACE(@queryCompletedCount, ' AND t.id = v_type','');
		END IF;
		IF(v_month = 0)THEN
            SET @queryBooking = REPLACE(@queryBooking, ' AND month(b.bookedTime) = v_month', '');
			SET @queryBookingCount = REPLACE(@queryBookingCount, ' AND month(b.bookedTime) = v_month', '');
            SET @queryCancellation = REPLACE(@queryCancellation, ' AND month(b.bookedTime) = v_month', '');
			SET @queryCancellationCount = REPLACE(@queryCancellationCount, ' AND month(b.bookedTime) = v_month', '');
            SET @queryCompleted = REPLACE(@queryCompleted, ' AND month(b.bookedTime) = v_month', '');
			SET @queryCompletedCount = REPLACE(@queryCompletedCount, ' AND month(b.bookedTime) = v_month', '');
		ELSE
			SET @queryBooking = REPLACE(@queryBooking, "v_month", v_month);
			SET @queryBookingCount = REPLACE(@queryBookingCount, "v_month", v_month);
			SET @queryCancellation = REPLACE(@queryCancellation, "v_month", v_month);
			SET @queryCancellationCount = REPLACE(@queryCancellationCount, "v_month", v_month);
			SET @queryCompleted = REPLACE(@queryCompleted, "v_month", v_month);
			SET @queryCompletedCount = REPLACE(@queryCompletedCount, "v_month", v_month);
		END IF;
		SET @queryBooking = REPLACE(@queryBooking, "v_cityId", v_cityId);
		SET @queryBooking = REPLACE(@queryBooking, "v_localityId", v_localityId);
		SET @queryBooking = REPLACE(@queryBooking, "v_type", v_type );
		SET @queryBooking = REPLACE(@queryBooking, "v_userId", v_userId);
		SET @queryCancellation = REPLACE(@queryCancellation, "v_cityId", v_cityId);
		SET @queryCancellation = REPLACE(@queryCancellation, "v_localityId", v_localityId);
		SET @queryCancellation = REPLACE(@queryCancellation, "v_type", v_type );
		SET @queryCancellation = REPLACE(@queryCancellation, "v_userId", v_userId);
		SET @queryCompleted = REPLACE(@queryCompleted, "v_cityId", v_cityId);
		SET @queryCompleted = REPLACE(@queryCompleted, "v_localityId", v_localityId);
		SET @queryCompleted = REPLACE(@queryCompleted, "v_type", v_type );
		SET @queryCompleted = REPLACE(@queryCompleted, "v_userId", v_userId);
		
		SET @queryBookingCount = REPLACE(@queryBookingCount, "v_cityId", v_cityId);
		SET @queryBookingCount = REPLACE(@queryBookingCount, "v_localityId", v_localityId);
		SET @queryBookingCount = REPLACE(@queryBookingCount, "v_type", v_type );
		SET @queryBookingCount = REPLACE(@queryBookingCount, "v_userId", v_userId);
		SET @queryCancellationCount = REPLACE(@queryCancellationCount, "v_cityId", v_cityId);
		SET @queryCancellationCount = REPLACE(@queryCancellationCount, "v_localityId", v_localityId);
		SET @queryCancellationCount = REPLACE(@queryCancellationCount, "v_type", v_type );
		SET @queryCancellationCount = REPLACE(@queryCancellationCount, "v_userId", v_userId);
		SET @queryCompletedCount = REPLACE(@queryCompletedCount, "v_cityId", v_cityId);
		SET @queryCompletedCount = REPLACE(@queryCompletedCount, "v_localityId", v_localityId);
		SET @queryCompletedCount = REPLACE(@queryCompletedCount, "v_type", v_type );
		SET @queryCompletedCount = REPLACE(@queryCompletedCount, "v_userId", v_userId);

		SET @queryBooking = CONCAT('SELECT * FROM (', CONCAT(@queryBooking, CONCAT(' UNION ALL ', CONCAT(@queryCancellation, CONCAT(' UNION ALL ', CONCAT(@queryCompleted, ') AS data ORDER BY bookingId DESC LIMIT LowerBound, v_count'))))));
		SET @queryBookingCount = CONCAT('SELECT  SUM(count) INTO @v_totalRecords FROM (', CONCAT(@queryBookingCount, CONCAT(' UNION ALL ', CONCAT(@queryCancellationCount, CONCAT(' UNION ALL ', CONCAT(@queryCompletedCount, ') AS data'))))));
		SET @queryBooking = REPLACE(@queryBooking, "LowerBound", LowerBound);
		SET @queryBooking = REPLACE(@queryBooking, "v_count", v_count);
		SET @queryCancellation = REPLACE(@queryCancellation, "LowerBound", LowerBound);
		SET @queryCancellation = REPLACE(@queryCancellation, "v_count", v_count);
		
        PREPARE stmt FROM @queryBooking;
		EXECUTE stmt;
        IF(v_pageNo = 1) THEN
			PREPARE stmt FROM @queryBookingCount;
			EXECUTE stmt;
			SET v_count = @v_totalRecords;
        ELSE
      		SET v_count = 0;
        END IF;
	END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `moveBookingData` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `moveBookingData`(IN p_bookingId MEDIUMTEXT)
BEGIN

	DELETE FROM booking WHERE status = 0 AND TIMESTAMPDIFF(MINUTE, bookedTime, NOW()) > 7;
	SELECT GROUP_CONCAT(id) INTO p_bookingId FROM booking WHERE TIMESTAMPDIFF(MINUTE, NOW(), toTime) < 6;
	INSERT INTO `huddil`.`booking_history`
		(`fromDateTime`, `toDateTime`, `bookedTime`, `price`, `paymentMethod`, `userId`, `facilityId`, `bookingId`, `paymentId`, `seats`)
		SELECT fromTime, toTime, bookedTime, totalPrice, paymentMethod, userId, facilityId, id, paymentId, seats FROM booking WHERE FIND_IN_SET(id, p_bookingId);
	DELETE FROM booking WHERE FIND_IN_SET(id, p_bookingId);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `paymentAdminDashboard` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `paymentAdminDashboard`(IN p_month INT, IN p_year INT, IN p_city VARCHAR(45), IN p_spName VARCHAR(45), IN p_spId INT, OUT p_online DOUBLE, 
OUT p_onlineCancel DOUBLE, OUT p_onlineCancelCharges DOUBLE, OUT p_offline DOUBLE, OUT p_offlineCancel DOUBLE, OUT p_tranCharge DOUBLE, OUT p_commission DOUBLE, OUT p_settlement DOUBLE)
BEGIN
	
    DECLARE v_transactionCharge DOUBLE;
    DECLARE v_spIds MEDIUMTEXT;
    DECLARE v_commission DOUBLE;
    DECLARE v_userType INT;

    SET v_transactionCharge = 0.02; /*2% should be denoted as 0.02*/
    SELECT id INTO v_userType FROM user_type WHERE name = 'service provider';
    IF(p_spId = -1) THEN
		SELECT SUM(price) INTO p_online FROM(
			SELECT totalPrice AS price, id FROM booking WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod <> 'offline' AND status = 3 UNION
			SELECT price, id FROM booking_history WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod <> 'offline') temp;
		SELECT SUM(price) INTO p_offline FROM(
			SELECT totalPrice AS price, id FROM booking WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod = 'offline' AND status = 3 UNION
			SELECT price, id FROM booking_history WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod = 'offline') temp;
		SELECT SUM(refundAmount) INTO p_onlineCancel FROM cancellation WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod <> 'offline';
		SELECT SUM(totalPrice) - SUM(refundAmount) INTO p_onlineCancelCharges FROM cancellation WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod <> 'offline';
		SELECT SUM(refundAmount) INTO p_offlineCancel FROM cancellation WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND paymentMethod = 'offline';
		IF(p_online IS NULL) THEN
			SET p_online = 0;
		END IF;
		IF(p_onlineCancel IS NULL) THEN
			SET p_onlineCancel = 0;
		END IF;
		IF(p_offline IS NULL) THEN
			SET p_offline = 0;
		END IF;
		IF(p_offlineCancel IS NULL) THEN
			SET p_offlineCancel = 0;
		END IF;
		IF(p_onlineCancelCharges IS NULL) THEN
			SET p_onlineCancelCharges = 0;
		END IF;

		SELECT SUM(commission) INTO p_commission FROM (SELECT SUM(totalPrice) * u.commission AS commission FROM booking b
			JOIN facility f ON f.id = b.facilityId
			JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND b.status = 3
			GROUP BY f.spUserId, u.commission
            UNION
            SELECT SUM(price) * u.commission AS commission FROM booking_history b
   			JOIN facility f ON f.id = b.facilityId
			JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year
			GROUP BY f.spUserId, u.commission
            UNION
            SELECT (SUM(totalPrice) - SUM(refundAmount)) * u.commission AS commission FROM cancellation b
			JOIN facility f ON f.id = b.facilityId
			JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year 
			GROUP BY f.spUserId, u.commission) AS temp;
		IF(p_commission IS NULL) THEN
			SET p_commission = 0;
		END IF;
		SET p_tranCharge = (p_online + p_onlineCancel + p_onlineCancelCharges) * v_transactionCharge;
		SET p_settlement = (p_online + p_onlineCancelCharges) - (p_commission + p_tranCharge);
    ELSEIF(p_spId = 0) THEN
		IF(p_city <> '0' AND p_spName <> '') THEN
			SELECT GROUP_CONCAT(DISTINCT u.userId) INTO v_spIds FROM facility f JOIN user_pref u ON f.spUserId = u.userId WHERE f.cityName = p_city AND u.emailId LIKE CONCAT('%', CONCAT(p_spName, '%')) AND u.userType = v_userType;
		ELSEIF(p_city <> '0') THEN
			SELECT GROUP_CONCAT(DISTINCT u.userId) INTO v_spIds FROM facility f JOIN user_pref u ON f.spUserId = u.userId WHERE f.cityName = p_city;
		ELSEIF(p_spName <> '') THEN
			SELECT GROUP_CONCAT(DISTINCT u.userId) INTO v_spIds FROM facility f JOIN user_pref u ON f.spUserId = u.userId WHERE u.emailId LIKE CONCAT('%', CONCAT(p_spName, '%')) AND u.userType = v_userType;
		ELSE 
			SET v_spIds = '';
		END IF;
		IF(v_spIds = '') THEN
			SELECT SUM(CASE WHEN paymentMethod <> 'offline' THEN total ELSE 0 END), 
					SUM(CASE WHEN paymentMethod = 'offline' THEN total ELSE 0 END) INTO p_online, p_offline FROM(
				SELECT totalPrice AS total, paymentMethod, id FROM booking WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year 
					AND status = 3 UNION
				SELECT price AS total, paymentMethod, id FROM booking_history WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year) AS temp;
			SELECT SUM(CASE WHEN paymentMethod <> 'offline' THEN refundAmount ELSE 0 END), 
					SUM(CASE WHEN paymentMethod = 'offline' THEN refundAmount ELSE 0 END), 
					SUM(CASE WHEN paymentMethod <> 'offline' THEN totalPrice - refundAmount ELSE 0 END) 
                    INTO p_onlineCancel, p_offlineCancel, p_onlineCancelCharges
				FROM cancellation WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year;
			IF(p_online IS NULL) THEN
				SET p_online = 0;
			END IF;
			IF(p_onlineCancel IS NULL) THEN
				SET p_onlineCancel = 0;
			END IF;
			IF(p_offline IS NULL) THEN
				SET p_offline = 0;
			END IF;
			IF(p_offlineCancel IS NULL) THEN
				SET p_offlineCancel = 0;
			END IF;
			IF(p_onlineCancelCharges IS NULL) THEN
				SET p_onlineCancelCharges = 0;
			END IF;
			SELECT SUM(commission) INTO p_commission FROM (SELECT SUM(totalPrice) * u.commission AS commission FROM booking b
				JOIN facility f ON f.id = b.facilityId
				JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year 
                WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND b.status = 3 GROUP BY f.spUserId, u.commission
                UNION
				SELECT SUM(price) * u.commission AS commission FROM booking_history b
				JOIN facility f ON f.id = b.facilityId
				JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year 
                WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year GROUP BY f.spUserId, u.commission
                UNION
				SELECT (SUM(totalPrice) - SUM(refundAmount)) * u.commission AS commission FROM cancellation b
				JOIN facility f ON f.id = b.facilityId
				JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year 
                WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year GROUP BY f.spUserId, u.commission) AS temp;
			IF(p_commission IS NULL) THEN
				SET p_commission = 0;
			END IF;
			SET p_tranCharge = (p_online + p_onlineCancel + p_onlineCancelCharges) * v_transactionCharge;
			SET p_settlement = (p_online + p_onlineCancelCharges) - (p_commission + p_tranCharge);
			
			DROP TABLE IF EXISTS paymentAmt;
			DROP TABLE IF EXISTS refundAmt;
			
            CREATE TABLE IF NOT EXISTS paymentAmt AS 
				SELECT spUserId AS pUserId, displayName AS pDName, commission AS pPercentage, 
					SUM(CASE WHEN paymentMethod <> 'offline' THEN total ELSE 0 END) AS onlinePay, 
                    SUM(CASE WHEN paymentMethod = 'offline' THEN total ELSE 0 END) AS offlinePay FROM
					(SELECT b.totalPrice AS total, f.spUserId, u.displayName, s.commission, b.paymentMethod, b.id FROM booking b
						JOIN facility f ON f.id = b.facilityId
						JOIN user_pref u ON u.userId = f.spUserId
                        JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year 
						WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND b.status = 3 UNION
					SELECT price AS total, f.spUserId, u.displayName, s.commission, b.paymentMethod, b.id FROM booking_history b
						JOIN facility f ON f.id = b.facilityId
						JOIN user_pref u ON u.userId = f.spUserId
                        JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year 
						WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year) AS temp GROUP BY pUserId, displayName, commission;
    
            CREATE TABLE IF NOT EXISTS refundAmt AS 
            SELECT f.spUserId AS rUserId, u.displayName AS rDName, s.commission AS rPercentage, 
            SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.refundAmount ELSE 0 END) AS onlineRef, 
            SUM(CASE WHEN c.paymentMethod = 'offline' THEN c.refundAmount ELSE 0 END) AS offlineRef,
            SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.totalPrice - c.refundAmount ELSE 0 END) AS onlineCancel FROM cancellation c
				JOIN facility f ON f.id = c.facilityId
				JOIN user_pref u ON u.userId = f.spUserId
                JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year 
				WHERE MONTH(c.bookedTime) = p_month AND YEAR(c.bookedTime) = p_year GROUP BY rUserId, u.displayName, s.commission;

			SELECT userId, dName, '' AS fName, '' AS lName, '' AS cName, 
            @onlinePay := IF(onlinePay IS NULL, 0 , onlinePay) AS onlinePay, 
            @onlineRef := IF(onlineRef IS NULL, 0 , onlineRef) AS onlineRef,
            @onlineCancelCharge := IF(onlineCancel IS NULL, 0, onlineCancel) AS onlineCancelCharge,
			@offlinePay := IF(offlinePay IS NULL, 0 , offlinePay) AS offlinePay, 
            @offlineRef := IF(offlineRef IS NULL, 0 , offlineRef) AS offlineRef, 
            ROUND(@tran := (@onlinePay + @onlineRef + @onlineCancelCharge) * v_transactionCharge, 2) AS tranCharge,
			ROUND(@commission := (@onlinePay + @offlinePay + @onlineCancelCharge) * percentage, 2) AS commission, 
            ROUND((@onlinePay + @onlineCancelCharge) - (@tran + @commission), 2) AS settlement  FROM 
				(SELECT pUserId AS userId, pDName AS dName, pPercentage AS percentage, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
				JOIN refundAmt r ON p.puserId = r.ruserId
				UNION
				SELECT pUserId AS userId, pDName AS dName, pPercentage AS percentage, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
				LEFT JOIN refundAmt r ON p.puserId = r.ruserId
				UNION
				SELECT rUserId AS userId, rDName AS dName, rPercentage AS percentage, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
				RIGHT JOIN refundAmt r ON p.puserId = r.ruserId) AS paymentReport;
		ELSE
			SELECT SUM(CASE WHEN paymentMethod <> 'offline' THEN total ELSE 0 END), 
				   SUM(CASE WHEN paymentMethod = 'offline' THEN total ELSE 0 END) INTO p_online, p_offline FROM(
				SELECT b.totalPrice AS total, b.paymentMethod, b.id FROM booking b JOIN facility f ON b.facilityId = f.id 
					WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND b.status = 3 AND FIND_IN_SET(f.spUserId, v_spIds) 
				UNION
				SELECT b.price AS total, b.paymentMethod, b.id FROM booking_history b JOIN facility f ON b.facilityId = f.id 
					WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND FIND_IN_SET(f.spUserId, v_spIds)) AS temp;

			SELECT  SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.refundAmount ELSE 0 END), 
					SUM(CASE WHEN c.paymentMethod = 'offline' THEN c.refundAmount ELSE 0 END),
 					SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.totalPrice - c.refundAmount ELSE 0 END) 
                    INTO p_onlineCancel, p_offlineCancel, p_onlineCancelCharges
				FROM cancellation c JOIN facility f ON c.facilityId = f.id 
                WHERE MONTH(c.bookedTime) = p_month AND YEAR(c.bookedTime) = p_year AND FIND_IN_SET(f.spUserId, v_spIds);
			IF(p_online IS NULL) THEN
				SET p_online = 0;
			END IF;
			IF(p_onlineCancel IS NULL) THEN
				SET p_onlineCancel = 0;
			END IF;
			IF(p_offline IS NULL) THEN
				SET p_offline = 0;
			END IF;
			IF(p_offlineCancel IS NULL) THEN
				SET p_offlineCancel = 0;
			END IF;
			IF(p_onlineCancelCharges IS NULL) THEN
				SET p_onlineCancelCharges = 0;
			END IF;

			SELECT SUM(commission) INTO p_commission FROM (
				SELECT SUM(totalPrice) * s.commission AS commission FROM booking b
					JOIN facility f ON f.id = b.facilityId
					JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year 
						WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND FIND_IN_SET(s.spUserId, v_spIds) AND b.status = 3
						GROUP BY f.spUserId, s.commission 
				UNION
				SELECT SUM(price) * s.commission AS commission FROM booking_history b
					JOIN facility f ON f.id = b.facilityId
					JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year 
                    WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND FIND_IN_SET(s.spUserId, v_spIds) GROUP BY f.spUserId, s.commission 
				UNION
				SELECT (SUM(totalPrice) - SUM(refundAmount)) * u.commission AS commission FROM cancellation b
					JOIN facility f ON f.id = b.facilityId
					JOIN sp_commission u ON u.spUserId = f.spUserId AND FIND_IN_SET(p_month, u.month) AND u.year = p_year 
					WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND FIND_IN_SET(u.spUserId, v_spIds) GROUP BY f.spUserId, u.commission) AS temp;
			IF(p_commission IS NULL) THEN
				SET p_commission = 0;
			END IF;
			SET p_tranCharge = (p_online + p_onlineCancel + p_onlineCancelCharges) * v_transactionCharge;
			SET p_settlement = (p_online + p_onlineCancelCharges) - (p_commission + p_tranCharge);

			DROP TABLE IF EXISTS paymentAmt;
			DROP TABLE IF EXISTS refundAmt;

            CREATE TABLE IF NOT EXISTS paymentAmt AS 
				SELECT spUserId AS pUserId, displayName AS pDName, commission AS pPercentage, 
                SUM(CASE WHEN paymentMethod <> 'offline' THEN total ELSE 0 END) AS onlinePay, 
                SUM(CASE WHEN paymentMethod = 'offline' THEN total ELSE 0 END) AS offlinePay FROM
					(SELECT b.totalPrice AS total, f.spUserId, u.displayName, s.commission, b.paymentMethod, b.id FROM booking b
						JOIN facility f ON f.id = b.facilityId
						JOIN user_pref u ON u.userId = f.spUserId
                        JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year
						WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND b.status = 3  AND FIND_IN_SET(u.userId, v_spIds) UNION
					SELECT price AS total, f.spUserId, u.displayName, s.commission, b.paymentMethod, b.id FROM booking_history b
						JOIN facility f ON f.id = b.facilityId
						JOIN user_pref u ON u.userId = f.spUserId
                        JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year
						WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND FIND_IN_SET(u.userId, v_spIds)) AS temp 
				GROUP BY pUserId, displayName, commission;

			CREATE TABLE IF NOT EXISTS refundAmt AS 
				SELECT f.spUserId AS rUserId, u.displayName AS rDName, s.commission AS rPercentage, 
                SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.refundAmount ELSE 0 END) AS onlineRef, 
                SUM(CASE WHEN c.paymentMethod = 'offline' THEN c.refundAmount ELSE 0 END) AS offlineRef,
				SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.totalPrice - c.refundAmount ELSE 0 END) AS onlineCancel FROM cancellation c
					JOIN facility f ON f.id = c.facilityId
					JOIN location l ON l.id = f.locationId
					JOIN user_pref u ON u.userId = f.spUserId
					JOIN sp_commission s ON s.spUserId = f.spUserId AND FIND_IN_SET(p_month, s.month) AND s.year = p_year
					WHERE MONTH(c.bookedTime) = p_month AND YEAR(c.bookedTime) = p_year AND FIND_IN_SET(u.userId, v_spIds) 
                GROUP BY rUserId, s.commission;

			SELECT userId, dName, '' AS fName, '' AS lName, '' AS cName, 
            @onlinePay := IF(onlinePay IS NULL, 0 , onlinePay) AS onlinePay, 
            @onlineRef := IF(onlineRef IS NULL, 0 , onlineRef) AS onlineRef,
            @onlineCancelCharge := IF(onlineCancel IS NULL, 0, onlineCancel) AS onlineCancelCharge,
			@offlinePay := IF(offlinePay IS NULL, 0 , offlinePay) AS offlinePay, 
            @offlineRef := IF(offlineRef IS NULL, 0 , offlineRef) AS offlineRef, 
            ROUND(@tran := (@onlinePay + @onlineRef + @onlineCancelCharge) * v_transactionCharge, 2) AS tranCharge,
			ROUND(@commission := (@onlinePay + @offlinePay + @onlineCancelCharge) * percentage, 2) AS commission, 
            ROUND((@onlinePay + @onlineCancelCharge) - (@tran + @commission), 2) AS settlement  FROM 
				(SELECT pUserId AS userId, pDName AS dName, pPercentage AS percentage, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
				JOIN refundAmt r ON p.puserId = r.ruserId
				UNION
				SELECT pUserId AS userId, pDName AS dName, pPercentage AS percentage, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
				LEFT JOIN refundAmt r ON p.puserId = r.ruserId
				UNION
				SELECT rUserId AS userId, rDName AS dName, rPercentage AS percentage, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
				RIGHT JOIN refundAmt r ON p.puserId = r.ruserId) AS paymentReport;

			DROP TABLE IF EXISTS paymentAmt;
			DROP TABLE IF EXISTS refundAmt;        
		END IF;
    ELSE
		SELECT commission INTO v_commission FROM sp_commission WHERE spUserId = p_spId AND FIND_IN_SET(p_month, month) AND year = p_year;
		SELECT SUM(CASE WHEN paymentMethod <> 'offline' THEN total ELSE 0 END), 
				SUM(CASE WHEN paymentMethod = 'offline' THEN total ELSE 0 END) INTO p_online, p_offline FROM(
			SELECT b.totalPrice AS total, b.paymentMethod, b.id FROM booking b JOIN facility f ON b.facilityId = f.id 
				WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND b.status = 3 AND f.spUserId = p_spId UNION
			SELECT b.price AS total, b.paymentMethod, b.id FROM booking_history b JOIN facility f ON b.facilityId = f.id 
				WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND f.spUserId = p_spId) AS temp;
		
        SELECT SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.refundAmount ELSE 0 END), 
				SUM(CASE WHEN c.paymentMethod = 'offline' THEN c.refundAmount ELSE 0 END),
                SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.totalPrice - c.refundAmount ELSE 0 END) 
                INTO p_onlineCancel, p_offlineCancel, p_onlineCancelCharges
			FROM cancellation c JOIN facility f ON c.facilityId = f.id WHERE MONTH(c.bookedTime) = p_month AND YEAR(c.bookedTime) = p_year AND f.spUserId = p_spId;
 		IF(p_online IS NULL) THEN
			SET p_online = 0;
		END IF;
		IF(p_onlineCancel IS NULL) THEN
			SET p_onlineCancel = 0;
		END IF;
		IF(p_offline IS NULL) THEN
			SET p_offline = 0;
		END IF;
		IF(p_offlineCancel IS NULL) THEN
			SET p_offlineCancel = 0;
		END IF;
		IF(p_onlineCancelCharges IS NULL) THEN
			SET p_onlineCancelCharges = 0;
		END IF;
        SET p_commission = (p_online + p_offline + p_onlineCancelCharges) * v_commission;
		IF(p_commission IS NULL) THEN
			SET p_commission = 0;
		END IF;
        SET p_tranCharge = (p_online + p_onlineCancel + p_onlineCancelCharges) * v_transactionCharge;
		SET p_settlement = (p_online + p_onlineCancelCharges) - (p_commission + p_tranCharge);
		
        DROP TABLE IF EXISTS paymentAmt;
		DROP TABLE IF EXISTS refundAmt;

		CREATE TABLE IF NOT EXISTS paymentAmt AS 
		SELECT pId, pTitle, pName, pCity, pUserId, 
				SUM(CASE WHEN paymentMethod <> 'offline' THEN total ELSE 0 END) AS onlinePay, 
                SUM(CASE WHEN paymentMethod = 'offline' THEN total ELSE 0 END) AS offlinePay FROM
			(SELECT f.title AS pTitle, l.name AS pName, b.paymentMethod, b.totalPrice AS total, f.id AS pId, 
				f.cityName AS pCity, f.spUserId AS pUserId, b.id FROM booking b
				JOIN facility f ON f.id = b.facilityId
				JOIN location l ON l.id = f.locationId
				WHERE MONTH(b.bookedTime) = p_month AND YEAR(b.bookedTime) = p_year AND b.status = 3 AND f.spUserId = p_spId UNION
			SELECT f.title AS pTitle, l.name AS pName, b.paymentMethod, b.price AS total, f.id AS pId, 
				f.cityName AS pCity, f.spUserId AS pUserId, b.id FROM booking_history b
				JOIN facility f ON f.id = b.facilityId
				JOIN location l ON l.id = f.locationId
				WHERE MONTH(bookedTime) = p_month AND YEAR(bookedTime) = p_year AND f.spUserId = p_spId) AS temp 
		GROUP BY pId, pTitle, pName, pCity, pUserId;

		CREATE TABLE IF NOT EXISTS refundAmt AS 
		SELECT f.id AS rId, f.title AS rTitle, l.name AS rName, f.cityName AS rCity, f.spUserId AS rUserId, 
				SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.refundAmount ELSE 0 END) AS onlineRef, 
                SUM(CASE WHEN c.paymentMethod = 'offline' THEN c.refundAmount ELSE 0 END) AS offlineRef,
				SUM(CASE WHEN c.paymentMethod <> 'offline' THEN c.totalPrice - c.refundAmount ELSE 0 END) AS onlineCancel FROM cancellation c
					JOIN facility f ON f.id = c.facilityId
					JOIN location l ON l.id = f.locationId
				WHERE MONTH(c.bookedTime) = p_month AND YEAR(c.bookedTime) = p_year AND f.spUserId = p_spId 
			GROUP BY f.id, f.title, l.name, f.cityName, f.spUserId;
            
		SELECT p_spId AS userId, '' AS dName, fName, lName, cName, 
        @onlinePay := IF(onlinePay IS NULL, 0 , onlinePay) AS onlinePay, 
        @onlineRef := IF(onlineRef IS NULL, 0 , onlineRef) AS onlineRef,
        @onlineCancelCharge := IF(onlineCancel IS NULL, 0, onlineCancel) AS onlineCancelCharge,
		@offlinePay := IF(offlinePay IS NULL, 0 , offlinePay) AS offlinePay, 
        @offlineRef := IF(offlineRef IS NULL, 0 , offlineRef) AS offlineRef, 
        ROUND(@tran := (@onlinePay + @onlineRef + @onlineCancelCharge) * v_transactionCharge, 2) AS tranCharge,
		ROUND(@commission := (@onlinePay + @offlinePay + @onlineCancelCharge) * v_commission, 2) AS commission, 
        ROUND((@onlinePay + @onlineCancelCharge) - (@tran + @commission), 2) AS settlement  FROM 
			(SELECT pTitle AS fName, pName AS lName, pCity AS cName, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
			JOIN refundAmt r ON p.pId = r.rID
			UNION
			SELECT pTitle AS fName, pName AS lName, pCity AS cName, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
			LEFT JOIN refundAmt r ON p.pId = r.rID
			UNION
			SELECT rTitle AS fName, rName AS lName, rCity AS cName, onlinePay, onlineRef, onlineCancel, offlinePay, offlineRef FROM paymentAmt p
			RIGHT JOIN refundAmt r ON p.pId = r.rID) AS paymentReport;

        DROP TABLE IF EXISTS paymentAmt;
		DROP TABLE IF EXISTS refundAmt;
    END IF;
    SET p_online = ROUND(p_online, 2);
    SET p_onlineCancel = ROUND(p_onlineCancel, 2);
    SET p_offline = ROUND(p_offline, 2);
    SET p_offlineCancel = ROUND(p_offlineCancel, 2);
    SET p_tranCharge = ROUND(p_tranCharge, 2);
    SET p_commission = ROUND(p_commission, 2);
    SET p_settlement = ROUND(p_settlement, 2);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `createBooking` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `createBooking`(IN p_fromDateTime TIMESTAMP, IN p_toDateTime TIMESTAMP, IN p_capacity INT, IN p_fId INT, IN p_sessionId VARCHAR(128), IN p_operation INT, 
			IN p_paymentMethod VARCHAR(45), IN p_paymentId VARCHAR(45), INOUT p_book INT, OUT p_result INT, OUT p_cost DOUBLE, OUT p_cgst DOUBLE, OUT p_sgst DOUBLE, OUT p_cgstCost DOUBLE, 
            OUT p_sgstCost DOUBLE, OUT p_offer DOUBLE, OUT p_totalCost DOUBLE)
BEGIN

	/*
		Input parameters - p_operation
		0 - perform calculation only
        1 - create a booking entry and return the bookingId
        2 - Confirrm the booking and update the payment details for the booking
        
		Output parameters - p_result values after the procedure execution
        -2 - p_capacity cannot be zero for co-working space
		0 - fromTime is after toTime
		1 - invalid sessionId
        2 - invalid userType
        3 - invalid facilityId
        4 - facility is not available for booking
        5 - required facility is under maintenance for the specified time
        6 - fromTime is before the facility openingTime or after the facility closing time
        7 - endTime is after the facility closingTime or before the facility opening time
        8 - co-wprking space enough seats are not available
        9 - co-working space enough seats are available
        10 - booking already exist for the specified facility at the specified time
        11 - specified facility is available for booking for the specified time
        12 - Unable to confirm the booking as the given bookingId is invalid
        13 - Unable to confirm booking as the bookingId and the userId does not match
        14 - Booking confirmed
        15 - Duplicate payment id
        16 - facility is closedon the selected dates
    */
    DECLARE v_userId INT;
    DECLARE v_userType INT;
    DECLARE v_minute INT;
    DECLARE v_hour INT;
    DECLARE v_day INT;
    DECLARE v_month INT;
    DECLARE v_fromDate DATE;
    DECLARE v_toDate DATE;
    DECLARE v_fromDay INT;
    DECLARE v_toDay INT;
    DECLARE v_fromTime TIME;
    DECLARE v_toTime TIME;
    DECLARE v_costPerHour DOUBLE;
    DECLARE v_costPerDay DOUBLE;
    DECLARE v_costPerMonth DOUBLE;
    DECLARE v_approverd INT;
    DECLARE v_verified INT;
    DECLARE v_pendingVerification INT;
    DECLARE v_verificationRejected INT;
	DECLARE v_status INT;
	DECLARE v_capacity INT;
    DECLARE v_confirmedId INT;
    DECLARE v_closedDays INT;
    DECLARE v_startWeekDay INT;
    DECLARE v_endWeekDay INT;
    DECLARE v_lastDayOfMonth DATE;
    DECLARE v_firstDayOfMonth DATE;
    DECLARE v_monthRollOver BOOLEAN;

    SELECT userId, userType INTO v_userId, v_userType FROM user_pref WHERE sessionId = p_sessionId;
    SELECT CAST(p_fromDateTime AS TIME), CAST(p_toDateTime AS TIME), CAST(p_fromDateTime AS DATE), CAST(p_toDateTime AS DATE), DAY(p_fromDateTime), DAY(p_toDateTime) 
		INTO v_fromTime, v_toTime, v_fromDate, v_toDate, v_fromDay, v_toDay;
	SELECT id INTO v_approverd FROM status WHERE name = 'Approved and Not Verified';
	SELECT id INTO v_verified FROM status WHERE name = 'Approved and Verified';
	SELECT id INTO v_pendingVerification FROM status WHERE name = 'Verify request';
	SELECT id INTO v_verificationRejected FROM status WHERE name = 'Reject Verify Request';
    SELECT id INTO v_confirmedId FROM booking_status WHERE name = 'confirmed';
    SELECT costPerHour, costPerDay, costPerMonth, status, CGST, SGST INTO v_costPerHour, v_costPerDay, v_costPerMonth, v_status, p_cgst, p_sgst
		FROM facility f 
		JOIN location l ON f.locationId = l.id
		JOIN city c ON c.id = l.cityId
		JOIN tax t ON t.id = c.taxId
		WHERE f.id = p_fId;
	SELECT TIMESTAMPDIFF(MINUTE, p_fromDateTime, p_toDateTime) % 60, TIMESTAMPDIFF(HOUR, p_fromDateTime, p_toDateTime) INTO v_minute, v_hour;
	SET v_lastDayOfMonth = LAST_DAY(p_toDateTime);
    SET v_firstDayOfMonth = ADDDATE(LAST_DAY(SUBDATE(v_fromDate, INTERVAL 1 MONTH)),1);
    IF(v_lastDayOfMonth = v_toDate AND v_firstDayOfMonth = v_fromDate) THEN
		SET v_monthRollOver = TRUE;
	ELSE
		SET v_monthRollOver = FALSE;
    END IF;
    SET v_day = 0;
    SET v_month = 0;
    IF(MONTH(v_fromDate) = MONTH(v_toDate) AND YEAR(v_fromDate) = YEAR(v_toDate) AND v_monthRollOver = FALSE) THEN
		SET v_day = DATEDIFF(p_toDateTime, p_fromDateTime) + 1;
        SET v_month = 0;
	ELSE
		IF(v_fromDay  = v_toDay) THEN
			SET v_day = 1;
		ELSEIF(v_fromDay <> DAY(ADDDATE(v_toDate, 1)) AND v_monthRollOver = FALSE) THEN
			IF(v_toDay > v_fromDay) THEN
				SET v_day = v_toDay - v_fromDay + 1;
			ELSE
				SET v_day = DAY(LAST_DAY(v_fromDate)) - v_fromDay + v_toDay + 1;
            END IF;
        END IF;
        IF(v_monthRollOver = TRUE) THEN
			SET v_toDate = ADDDATE(v_toDate, 1);
        END IF;
        IF(MONTH(v_toDate) > MONTH(v_fromDate)) THEN
			SET v_month = MONTH(v_toDate) - MONTH(v_fromDate);
		ELSE
			SET v_month = 12 - MONTH(v_fromDate) + MONTH(v_toDate);
        END IF;
        SET v_fromDay = DAY(LAST_DAY(v_toDate)) + 1;
        SELECT DATEDIFF(v_toDate, v_fromDate);
        IF(DATEDIFF(v_toDate, v_fromDate) > 365) THEN
			SET v_month  = v_month % 12;
			SET v_month = v_month + ((YEAR(v_toDate) - YEAR(v_fromDate)) * 12);
		END IF;
    END IF;
	IF(v_day > 0) THEN
		SET v_closedDays = huddil.getClosedDays(DATE_SUB(v_toDate, INTERVAL (v_day - 1) DAY), v_toDate, p_fId);
		SET v_day = v_day - v_closedDays;
	END IF;
    SELECT DATE_ADD(p_fromDateTime, INTERVAL 1 SECOND), DATE_SUB(p_toDateTime, INTERVAL 1 SECOND) INTO p_fromDateTime, p_toDateTime;
    SET v_startWeekDay = DAYOFWEEK(p_fromDateTime);
    SET v_endWeekDay = DAYOFWEEK(p_toDateTime);
    
    IF(p_fromDateTime < NOW() OR p_toDateTime < NOW()) THEN
		SET p_result = -1;
    ELSEIF(p_fromDateTime > p_toDateTime) THEN
		SET p_result = 0;
    ELSEIF(p_sessionId <> '0' AND v_userId IS NULL) THEN
		SET p_result = 1;
	ELSEIF(p_sessionId <> '0' AND v_userType <> (SELECT id FROM user_type WHERE name = 'consumer') AND v_userType <> (SELECT id FROM user_type WHERE name = 'service provider')) THEN
		SET p_result = 2;
	ELSEIF((SELECT id FROM facility WHERE id = p_fId) IS NULL) THEN
		SET p_result = 3;
	ELSEIF(v_status <> v_approverd AND v_status <> v_verified AND v_status <> v_pendingVerification AND v_status <> v_verificationRejected) THEN
		SET p_result = 4;
	ELSEIF((SELECT id FROM facility_under_maintenance WHERE 
			(p_fromDateTime BETWEEN fromDateTime AND toDateTime OR 
			p_toDateTime BETWEEN fromDateTime AND toDateTime OR 
			fromDateTime BETWEEN p_fromDateTime AND p_toDateTime) AND facilityId = p_fId) IS NOT NULL) THEN
		SET p_result = 5;
	ELSEIF((SELECT openingTime FROM facility_timing WHERE facilityId = p_fId AND weekDay = v_startWeekDay) = '00:00:00' OR (SELECT closingTime FROM facility_timing WHERE facilityId = p_fId AND weekDay = v_endWeekDay) = '00:00:00') THEN
		SET p_result = 16;
	ELSEIF(v_fromTime <> '00:00:00' AND (SELECT id FROM facility_timing WHERE (v_fromTime >= openingTime AND v_fromTime < closingTime) AND facilityId = p_fId AND weekDay = v_startWeekDay) IS NULL) THEN
		SET p_result = 6;
    ELSEIF(v_fromTime <> '00:00:00' AND (SELECT id FROM facility_timing WHERE (v_toTime <= closingTime AND v_toTime >= openingTime) AND facilityId = p_fId AND weekDay = v_endWeekDay) IS NULL) THEN
		SET p_result = 7;
	ELSEIF((SELECT typeName FROM facility WHERE id = p_fId) = 'Co-Working Space') THEN
        IF(p_capacity = 0) THEN
			SET p_result = -2;
        ELSE
			SELECT SUM(seats) INTO v_capacity FROM booking WHERE 
					(p_fromDateTime BETWEEN fromTime AND toTime OR 
					p_toDateTime BETWEEN fromTime AND toTime OR 
					fromTime BETWEEN p_fromDateTime AND p_toDateTime) AND facilityId = p_fId;
			IF(v_capacity IS NULL) THEN
				SET v_capacity = 0;
			END IF;
			IF(((SELECT capacity FROM facility WHERE id = p_fId) - v_capacity) < p_capacity) THEN
				SET p_result = 8;
			ELSE
				SET p_result = 9;
			END IF;
        END IF;
	ELSE
		IF(p_operation <> 2 AND (SELECT count(id) FROM booking WHERE 
			(p_fromDateTime BETWEEN fromTime AND toTime OR 
			p_toDateTime BETWEEN fromTime AND toTime OR 
			fromTime BETWEEN p_fromDateTime AND p_toDateTime) AND facilityId = p_fId) <> 0) THEN
			SET p_result = 10;
		ELSE
			SET p_result = 11;
		END IF;
	END IF;
    IF(p_result = 9) THEN
		IF(v_fromDate = v_toDate) THEN
			SET p_cost = v_costPerDay * p_capacity;
		ELSE			
            IF(v_month <> 0) THEN
				SET p_cost = v_day * v_costPerDay;
				IF(p_cost > v_costPerMonth) THEN
					SET p_cost = (v_month + 1) * v_costPerMonth;
				ELSE
					SET p_cost = p_cost + (v_month * v_costPerMonth);
				END IF;
			ELSE
				SET p_cost = v_day * v_costPerDay;
                IF(p_cost > v_costPerMonth) THEN
					SET p_cost = v_costPerMonth;
                END IF;
            END IF;
            SET p_cost = p_cost * p_capacity;
		END IF;
    ELSEIF(p_result = 11) THEN
		IF(v_fromDate = v_toDate) THEN
			IF(v_fromTime = v_toTime AND v_fromTime = 0) THEN
				SET p_cost = v_costPerDay;
			ELSE
				IF(v_minute > 0) THEN
					SET v_hour = v_hour + 1;
                END IF;
				SET p_cost = v_hour * v_costPerHour;
                IF(p_cost > v_costPerDay) THEN
					SET p_cost = v_costPerDay;
                END IF;
			END IF;
		ELSE
            IF(v_month <> 0) THEN
				SET p_cost = v_day * v_costPerDay;
                IF(p_cost > v_costPerMonth) THEN
					SET p_cost = (v_month + 1) * v_costPerMonth;
				ELSE
					SET p_cost = p_cost + (v_month * v_costPerMonth);
                END IF;
			ELSE
				SET p_cost = v_day * v_costPerDay;
                IF(p_cost > v_costPerMonth) THEN
					SET p_cost = v_costPerMonth;
                END IF;
            END IF;            
		END IF;
    END IF;
    IF(v_userType = (SELECT id FROM user_type WHERE name = 'service provider')) THEN
		SET p_cost = 0;
		SET p_cgst = 0;
		SET p_sgst = 0;
		SET p_cgstCost = 0;
		SET p_sgstCost = 0;
		SET p_offer = 0;
		SET p_totalCost = 0;   
    ELSE
		SET p_cgstCost = p_cost * p_cgst / 100;
		SET p_sgstCost = p_cost * p_sgst / 100;
		SET p_totalCost = p_cost + p_cgstCost + p_sgstCost;
		SELECT price INTO p_offer FROM facility_offers WHERE facilityId = p_fId AND startDate <= v_fromDate AND endDate >= v_toDate;
		IF(p_offer IS NULL) THEN
			SET p_offer = 0;
		ELSE
			SET p_offer = p_cost * p_offer / 100;
		END IF;
		SET p_totalCost = TRUNCATE(p_totalCost - p_offer, 2);
    END IF;
	IF((p_result = 9 OR p_result = 11) AND p_operation = 1) THEN
		INSERT INTO `booking` (`fromTime`, `toTime`, `seats`, `price`, `totalPrice`, `userId`, `facilityId`, `status`, `cancellationPolicyId`)
			SELECT DATE_SUB(p_fromDateTime, INTERVAL 1 SECOND), DATE_ADD(p_toDateTime, INTERVAL 1 SECOND), p_capacity, p_cost, p_totalCost, v_userId, p_fId, 0, cancellationPolicyId from facility WHERE id = p_fId;
		SET p_book = LAST_INSERT_ID();
        IF(p_paymentMethod = 'offline') THEN
			UPDATE booking SET paymentMethod = p_paymentMethod, status = 3 WHERE id = p_book;
        END IF;
	ELSEIF(((p_result = 9 OR p_result = 11) AND p_operation = 2)) THEN
		IF((SELECT id FROM booking WHERE id = p_book) IS NULL) THEN
			SET p_result = 12;
		ELSEIF((SELECT userId FROM booking WHERE id = p_book) <> v_userId) THEN
			SET p_result = 13;
        ELSE
			IF((SELECT id FROM booking WHERE paymentId = p_paymentId) IS NULL) THEN
				UPDATE booking SET paymentMethod = p_paymentMethod, paymentId = p_paymentId, status = 3 WHERE id = p_book;
				SET p_result = 14;
			ELSE
                SET p_result = 15;
            END IF;
        END If;
	END IF;
    IF(p_book <> 0 AND (SELECT typeName FROM facility WHERE id = p_fId) = 'Co-Working Space' AND v_userType <> (SELECT id FROM user_type WHERE name = 'service provider')) THEN
		UPDATE booking SET status = 1 WHERE id = p_book;
    END IF;
	SELECT f.title, f.typeName, f.cityName, f.localityName, l.address, 
		s.displayName AS spName, s.emailId AS spEmailId, s.mobileNo AS spMobileNo, s.mobileNoVerified AS spMobileVerified,
		IF(c.displayName IS NULL, '', c.displayName) AS cName, IF(c.emailId IS NULL, '', c.emailId) AS cEmailId, IF(c.mobileNo IS NULL, '', c.mobileNo) AS cMobileNo, IF(c.mobileNoVerified IS NULL, false, true) AS cMobileVerified
	FROM facility f 
	JOIN location l ON f.locationId = l.id
	JOIN user_pref s ON s.userId = f.spUserId
	LEFT JOIN user_pref c ON c.userId = v_userId
	WHERE f.id = p_fId;
    SET p_cost = TRUNCATE(p_cost, 2);
    SET p_cgst = TRUNCATE(p_cgst, 2);
    SET p_sgst = TRUNCATE(p_sgst, 2);
    SET p_cgstCost = TRUNCATE(p_cgstCost, 2);
    SET p_sgstCost = TRUNCATE(p_sgstCost, 2);
    SET p_offer = TRUNCATE(p_offer, 2);
    SET p_totalCost = TRUNCATE(p_totalCost, 2);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `showAvailableFacilities` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `showAvailableFacilities`(IN v_sessionId VARCHAR(100),IN v_operation INT, IN v_fromDateTime TIMESTAMP, 
IN v_toDateTime TIMESTAMP, IN v_minCost DOUBLE, IN v_maxCost DOUBLE, IN v_maxCapacity INT, IN v_typeId INT, IN v_cityId INT, IN v_localityId INT, 
IN v_offers INT, IN v_amenity VARCHAR(50), IN v_pageNo INT, INOUT v_counting INT, OUT v_result INT)
BEGIN
    
 /*v_operation = 1 -> Show Available Facilities for Consumer*/
/*v_operation = 2 -> Show all Facilities of SP*/
/*v_operation = 3 -> Getting Favorite Facilities of Consumer*/
/*v_operation = 4 -> Getting All the Facilities For Advisor*/

DECLARE p_fromDateTime TIMESTAMP;
DECLARE p_toDateTime TIMESTAMP;
DECLARE v_lowerBound INT;
DECLARE v_count INT;
DECLARE v_totalRecords INT;
DECLARE v_minCapacity INT;
	DECLARE v_userType INT;
SET v_totalRecords = 0;
SET v_result = 0;
SET v_lowerBound = (v_pageNo - 1) * v_counting;

SET v_minCapacity = v_maxCapacity;
SET v_maxCapacity = v_minCapacity * 1.5;


SELECT userType INTO v_userType FROM user_pref WHERE sessionId = v_sessionId;
    IF(v_operation <> 1 AND v_userType IS NULL) THEN
		SET v_result = -1;
	ELSEIF(v_operation = 1)THEN
SELECT DATE_ADD(v_fromDateTime, INTERVAL 1 SECOND), DATE_SUB(v_toDateTime, INTERVAL 1 SECOND) INTO p_fromDateTime, p_toDateTime;

	SET @queryOne = "SELECT COUNT(DISTINCT f.id) INTO @v_totalRecords FROM huddil.facility f JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l on l.name = f.localityName JOIN huddil.location lo on lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName JOIN huddil.facility_photo p ON p.facilityId = f.id JOIN huddil.facility_amenity a ON a.facilityId = f.id _join";
    SET @query = "SELECT DISTINCT f.id, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailId, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT a.amenityId) as Amenities, GROUP_CONCAT(DISTINCT p.imgPath) as imgPath FROM huddil.facility f JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l on l.name = f.localityName JOIN huddil.location lo on lo.id = f.locationId JOIN huddil.facility_type t ON t.name = f.typeName JOIN huddil.facility_photo p ON p.facilityId = f.id JOIN huddil.facility_amenity a ON a.facilityId = f.id _join";
	
    IF(v_offers != 0)THEN
		SET @queryOne = REPLACE(@queryOne, ' _join','JOIN huddil.facility_offers o ON o.facilityId = f.id');
		SET @query = REPLACE(@query, ' _join','JOIN huddil.facility_offers o ON o.facilityId = f.id');
	ELSE
		SET @queryOne = REPLACE(@queryOne, ' _join', '');
		SET @query = REPLACE(@query, ' _join', '');
	END IF;
    
    IF(p_fromDateTime <> '' && p_toDateTime <> '')THEN	
		SET @queryOne = CONCAT(@queryOne, ' WHERE f.id NOT IN(SELECT b.facilityId FROM huddil.booking b WHERE (b.toTime >= \'',p_fromDateTime,'\'','  && b.fromTime <= \'',p_toDateTime,'\'',')) AND f.id NOT IN(SELECT m.facilityId FROM huddil.facility_under_maintenance m WHERE (m.toDateTime >= CAST(\'',p_fromDateTime,'\'',' AS DATE) && m.fromDateTime <= CAST(\'',p_toDateTime,'\'',' AS DATE)))');
		SET @query = CONCAT(@query, ' WHERE f.id NOT IN(SELECT b.facilityId FROM huddil.booking b WHERE (b.toTime >= \'',p_fromDateTime,'\'','  && b.fromTime <= \'',p_toDateTime,'\'',')) AND f.id NOT IN(SELECT m.facilityId FROM huddil.facility_under_maintenance m WHERE (m.toDateTime >= CAST(\'',p_fromDateTime,'\'',' AS DATE) && m.fromDateTime <= CAST(\'',p_toDateTime,'\'',' AS DATE)))');
	END IF;
    IF(v_cityId != 0 OR v_localityId != 0)THEN
		
		SET @queryOne = CONCAT(@queryOne, ' AND c.id = v_cityId');
		SET @query = CONCAT(@query, ' AND c.id = v_cityId');
	END IF;
    IF(v_localityId != 0)THEN
		
		SET @queryOne = CONCAT(@queryOne, ' AND l.id = v_localityId');
		SET @query = CONCAT(@query, ' AND l.id = v_localityId');
	END IF;
    
    IF(v_amenity = '')THEN
		
        SET @queryOne = CONCAT(@queryOne, ' ');
		SET @query = CONCAT(@query, ' ');
	ELSE
		SELECT (LENGTH(v_amenity) - LENGTH(REPLACE(v_amenity, ',', '')) + 1 ) INTO v_count;
        
        SET @queryOne = CONCAT(@queryOne, ' AND f.id IN (SELECT facilityId FROM huddil.facility_amenity WHERE amenityId IN(v_amenity) group by facilityId having count(facilityId) = v_count)');
		SET @query = CONCAT(@query, ' AND f.id IN (SELECT facilityId FROM huddil.facility_amenity WHERE amenityId IN(v_amenity) group by facilityId having count(facilityId) = v_count)');
	END IF;
    
    IF(v_maxCapacity !=0)THEN
    
		SET @queryOne = CONCAT(@queryOne, ' AND f.capacity BETWEEN v_minCapacity AND v_maxCapacity');
		SET @query = CONCAT(@query, ' AND f.capacity BETWEEN v_minCapacity AND v_maxCapacity');
	END IF;
	
    IF(v_maxCost !=0)THEN
    
		SET @queryOne = CONCAT(@queryOne, ' AND f.costPerDay BETWEEN v_minCost AND v_maxCost');
		SET @query = CONCAT(@query, ' AND f.costPerDay BETWEEN v_minCost AND v_maxCost');
	END IF;
    
    IF(v_typeId !=0)THEN
    
		SET @queryOne = CONCAT(@queryOne, ' AND t.id = v_typeId');
		SET @query = CONCAT(@query, ' AND t.id = v_typeId');
        
    END IF;
    
    SET @queryOne = CONCAT(@queryOne, ' AND (f.status = 7 OR f.status = 8 OR f.status = 5)');
    SET @query = CONCAT(@query, ' AND (f.status = 7 OR f.status = 8 OR f.status = 5) GROUP by f.id order by f.averageRating desc LIMIT v_lowerBound, v_counting');
    
	IF(p_fromDateTime <> '' && p_toDateTime <> '')THEN
		
        SET @queryOne = REPLACE(@queryOne, 'p_fromDateTime', p_fromDateTime);
        SET @queryOne = REPLACE(@queryOne, 'p_toDateTime', p_toDateTime);
        
		SET @query = REPLACE(@query, 'p_fromDateTime', p_fromDateTime);
		SET @query = REPLACE(@query, 'p_toDateTime', p_toDateTime);
	END IF;
    IF(v_maxCost != 0)THEN
    
		SET @queryOne = REPLACE(@queryOne, 'v_minCost', v_minCost);
		SET @queryOne = REPLACE(@queryOne, 'v_maxCost', v_maxCost);
        
		SET @query = REPLACE(@query, 'v_minCost', v_minCost);
		SET @query = REPLACE(@query, 'v_maxCost', v_maxCost);
	END IF;
	
    SET @queryOne = REPLACE(@queryOne, 'v_minCapacity', v_minCapacity);
    SET @queryOne = REPLACE(@queryOne, 'v_maxCapacity', v_maxCapacity);
    SET @queryOne = REPLACE(@queryOne, 'v_cityId', v_cityId);
	SET @queryOne = REPLACE(@queryOne, 'v_localityId', v_localityId);
    
	SET @query = REPLACE(@query, 'v_minCapacity', v_minCapacity);
    SET @query = REPLACE(@query, 'v_maxCapacity', v_maxCapacity);
    SET @query = REPLACE(@query, 'v_cityId', v_cityId);
	SET @query = REPLACE(@query, 'v_localityId', v_localityId);

	SET @query = REPLACE(@query, 'v_counting', v_counting);
    
    IF(v_amenity <> '')THEN
		
        SET @queryOne = REPLACE(@queryOne, 'v_amenity', v_amenity);
		SET @queryOne = REPLACE(@queryOne, 'v_count', v_count);
        
		SET @query = REPLACE(@query, 'v_amenity', v_amenity);
		SET @query = REPLACE(@query, 'v_count', v_count);
	END IF;
    
    SET @queryOne = REPLACE(@queryOne, 'v_typeId', v_typeId);
    SET @query = REPLACE(@query, 'v_typeId', v_typeId);
    
    SET @query = REPLACE(@query, 'v_lowerBound', v_lowerBound);

    PREPARE stmt FROM @query;
    EXECUTE stmt;
    
    PREPARE stmt FROM @queryOne;
    EXECUTE stmt;
    
    SET v_counting = @v_totalRecords;
    

/*Facility Listing for Service Provider*/
ELSEIF(v_operation = 2)THEN
    
    SELECT DISTINCT f.id, f.spUserId, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, 
	f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailId, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, 
	f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, 
	GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath FROM huddil.facility f 
		JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
		JOIN huddil.facility_amenity a ON a.facilityId = f.id 
		JOIN huddil.amenity am ON am.id = a.amenityId 
		JOIN huddil.city c ON c.name = f.cityName 
		JOIN huddil.locality l ON l.name = f.localityName 
		JOIN huddil.location lo ON lo.id = f.locationId
		JOIN huddil.user_pref p ON p.userId = f.spUserId 
		WHERE p.sessionId = v_sessionId AND f.status > -1 group by f.id LIMIT v_lowerBound, v_counting;
        
        IF(v_pageNo = 1)THEN
			SELECT COUNT(DISTINCT f.id) INTO v_counting FROM huddil.facility f 
				JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
				JOIN huddil.facility_amenity a ON a.facilityId = f.id 
				JOIN huddil.amenity am ON am.id = a.amenityId 
				JOIN huddil.city c ON c.name = f.cityName 
				JOIN huddil.locality l ON l.name = f.localityName 
				JOIN huddil.location lo ON lo.id = f.locationId
				JOIN huddil.user_pref p ON p.userId = f.spUserId 
					WHERE p.sessionId = v_sessionId AND f.status > -1;
		END IF;

/*Favorites Facility Listing for Consumer*/
ELSEIF(v_operation = 3)THEN

	SELECT f.id, f.title, f.description, f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, 
    f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailId, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, 
    f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, 
    GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath FROM huddil.facility f 
		JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
		JOIN huddil.facility_amenity a ON a.facilityId = f.id 
		JOIN huddil.amenity am ON am.id = a.amenityId 
		JOIN huddil.city c ON c.name = f.cityName 
		JOIN huddil.locality l ON l.name = f.localityName 
		JOIN huddil.location lo ON lo.id = f.locationId 
		RIGHT JOIN huddil.favorites fa ON fa.facilityId = f.id 
		JOIN huddil.user_pref p ON p.userId = fa.userId
			WHERE p.sessionId = v_sessionId AND f.status != -1 group by f.id LIMIT v_lowerBound, v_counting;
    
    IF(v_pageNo =1)THEN
		SELECT COUNT(DISTINCT f.id) INTO v_counting FROM huddil.facility f 
			JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
			JOIN huddil.facility_amenity a ON a.facilityId = f.id 
			JOIN huddil.amenity am ON am.id = a.amenityId 
			JOIN huddil.city c ON c.name = f.cityName 
			JOIN huddil.locality l ON l.name = f.localityName 
			JOIN huddil.location lo ON lo.id = f.locationId 
			RIGHT JOIN huddil.favorites fa ON fa.facilityId = f.id 
			JOIN huddil.user_pref p ON p.userId = fa.userId
				WHERE p.sessionId = v_sessionId AND f.status != -1;
	END IF;

/*Facility Listing for Advisor*/
ELSEIF(v_operation = 4)THEN
		
    SELECT f.id, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, 
    f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailId, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, 
    f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, 
    GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath FROM huddil.facility f 
		JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
		JOIN huddil.user_pref p 
		JOIN huddil.facility_amenity a ON a.facilityId = f.id 
		JOIN huddil.amenity am ON am.id = a.amenityId 
		JOIN huddil.city c ON c.name = f.cityName 
		JOIN huddil.locality l ON l.name = f.localityName 
		JOIN huddil.location lo ON lo.id = f.locationId 
					WHERE p.sessionId = v_sessionId AND (f.status = 1 OR f.status = 2 OR f.status = 5 OR f.status =6) group by f.id order by f.dateTime desc LIMIT v_lowerBound, v_counting;
    
    IF(v_pageNo = 1)THEN
		
		SELECT COUNT(DISTINCT f.id) INTO v_counting FROM huddil.facility f 
			JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
			JOIN huddil.user_pref p 
			JOIN huddil.facility_amenity a ON a.facilityId = f.id 
			JOIN huddil.amenity am ON am.id = a.amenityId 
			JOIN huddil.city c ON c.name = f.cityName 
			JOIN huddil.locality l ON l.name = f.localityName 
			JOIN huddil.location lo ON lo.id = f.locationId 
						WHERE p.sessionId = v_sessionId AND (f.status = 1 OR f.status = 2 OR f.status = 5 OR f.status =6);
	END IF;
		
		END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `performCancellation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `performCancellation`(IN p_type INT, IN p_bookingId INT, IN p_facilityId INT, IN p_sessionId VARCHAR(128), IN p_reason VARCHAR(255), 
		IN p_operation INT, IN p_fromDateTime TIMESTAMP, IN p_toDateTime TIMESTAMP, OUT p_cancel INT, OUT p_result INT, OUT p_refund DOUBLE, OUT p_cancellationPrice DOUBLE, 
        OUT p_totalPrice DOUBLE, OUT p_count INT)
BEGIN

	/*
		p_type - operation performed
        1 - Cancel a booking
        2 - Add a facility under maintenance
        3 - Stop a facility
        
        p_operation - activity needs to be performed
        1 - calculate refund for a single booking
        2 - confirm and cancel a single booking
        3 - check for bookings before scheduling for maintenance
        4 - calculate refund before scheduling for maintenance
        5 - confirm, cancel all bookings and schedule for maintenance
        
        p_result will contain the result of the procdure execution
		1 - invalid sessionId
        2 - invalid userType
        3 - invalid bookingId
        4 - invalid facilityId
        5 - invalid input type
        6 - paymnet in-process
        7 - meeting is in progress, cannot cancel the booking
        8 - booking status is already cancelled
        9 - user does not permisson to cancel the booking
        10 - payment mode is offline, so no refund
        11 - single ofline booking is cancelled
		12 - paymnet mode is not offline, refund amount is calculated
		13 - single online booking is cancelled
		14 - specified facility is not in approved or verified status
		15 - current user id not the owner of the facility
		16 - there are no online bookings for the give time period, hence no refund will be made
		17 - there are online bookings for the give time period, hence refund will be made
		18 - single/ multiple cancellations are made and facility is marked for maintenace for the specified period
    */
    
    DECLARE v_userId INT;
    DECLARE v_userType INT;
    DECLARE v_consumerTypeId INT;
    DECLARE v_spTypeId INT;
    DECLARE v_facilityTd INT;
    DECLARE v_bookedUserId INT;
    DECLARE v_days INT;
    DECLARE v_cancellationPolicyId INT;
    DECLARE v_cancellationCharge DOUBLE;
	DECLARE v_bookingIds MEDIUMTEXT;
    DECLARE v_approvedStatus INT;
    DECLARE v_verifiedStatus INT;
    DECLARE v_pendStatus INT;
    DECLARE v_deactivateStatus INT;
    DECLARE v_veriAndDeacStatus INT;
    DECLARE v_spUserId INT;
    DECLARE v_paymentMode VARCHAR(45);
    DECLARE v_confirmedStatus INT;
    DECLARE v_pendingStatus INT;
    DECLARE v_basePrice DOUBLE;
    DECLARE v_bookingStatus INT;
    
	SELECT userId, userType INTO v_userId, v_userType FROM user_pref WHERE sessionId = p_sessionId;
    SELECT id INTO v_consumerTypeId FROM user_type WHERE name = 'consumer';
    SELECT id INTO v_spTypeId FROM user_type WHERE name = 'service provider';
    SELECT id INTO v_confirmedStatus FROM booking_status WHERE name = 'confirmed';
    SELECT id INTO v_pendingStatus FROM booking_status WHERE name = 'pending';
	IF(p_bookingId <> 0) THEN
		SELECT facilityId, status INTO v_facilityTd, v_bookingStatus FROM booking WHERE id = p_bookingId;
    ELSE
		SET v_facilityTd = p_facilityId;
    END IF;
    IF(v_userId IS NULL) THEN
		SET p_result = 1;
	ELSEIF(v_userType <> v_consumerTypeId AND v_userType <> v_spTypeId) THEN
		SET p_result = 2;
	ELSEIF(p_type = 1 AND (SELECT id FROM booking WHERE id = p_bookingId) IS NULL) THEN
		SET p_result = 3;
	ELSEIF(p_type <> 4 AND v_facilityTd IS NULL) THEN
		SET p_result = 4;
	ELSEIF(p_type = 1 AND (SELECT status FROM booking WHERE id = p_bookingId) = 0) THEN
		SET p_result = 6;
	ELSEIF((SELECT COUNT(id) FROM booking WHERE fromTime <= NOW() AND toTime > NOW() AND id = p_bookingId AND status = 1) <> 0)THEN
		SET p_result = 7;
	ELSEIF(p_type <> 4 AND v_bookingStatus <> v_confirmedStatus AND v_bookingStatus <> v_pendingStatus) THEN
		SET p_result = 8;
	ELSE
		IF(p_type = 1 AND (v_userType = v_consumerTypeId OR v_userType = v_spTypeId)) THEN
			SELECT userId INTO v_bookedUserId FROM booking WHERE id = p_bookingId;
            SELECT f.spUserId INTO v_spUserId FROM facility f JOIN booking b ON f.id = b.facilityId WHERE b.id = p_bookingId;
			IF(v_bookedUserId <> v_userId AND v_spUserId <> v_userId) THEN
				SET p_result = 9;
            ELSE
				SELECT paymentMethod INTO v_paymentMode FROM booking WHERE id = p_bookingId AND (status = v_confirmedStatus OR status = v_pendingStatus);
				IF(p_operation = 1 AND v_paymentMode = 'Offline') THEN
					SET p_result = 10;
				ELSEIF( p_operation = 2 AND v_paymentMode = 'Offline') THEN
					SET p_result = 11;
				ELSEIF(p_operation = 1 AND v_paymentMode <> 'Offline') THEN
					SET p_result = 12;
				ELSEIF(p_operation = 2 AND v_paymentMode <> 'Offline') THEN
					SET p_result = 13;
				END IF;
                IF(p_result = 12 OR p_result = 13) THEN
					SELECT price, totalPrice, HOUR(TIMEDIFF(fromTime, NOW())) / 24, cancellationPolicyId INTO 
						v_basePrice, p_totalPrice, v_days, v_cancellationPolicyId FROM booking WHERE id = p_bookingId;
					IF(v_bookedUserId = v_userId) THEN
						SELECT IF(v_days >= duration1, percentage1, IF(v_days >= duration2, percentage2, percentage3)) INTO v_cancellationCharge FROM facility_cancellation_charges WHERE id = v_cancellationPolicyId;
						IF(v_cancellationCharge = 0) THEN
							SET p_refund = 0;
						ELSE
							SET p_refund = v_basePrice - (v_basePrice * v_cancellationCharge / 100) + p_totalPrice - v_basePrice;
                        END IF;
					ELSEIF(v_spUserId = v_userId) THEN
						SET p_refund = p_totalPrice;
                    END IF;
                    SET p_cancellationPrice = p_totalPrice - p_refund;
				ELSEIF(p_result = 11) THEN
					SELECT totalPrice INTO p_refund FROM booking WHERE id = p_bookingId;
				END IF;
				IF(p_result = 11 OR p_result = 13) THEN
					INSERT INTO `huddil`.`cancellation`
					(`bookedFrom`, `bookedTo`, `bookedTime`, `seats`, `price`, `totalPrice`, `refundAmount`, `paymentId`, `paymentMethod`, `cancellationPolicyId`, `facilityId`, `bookedUserId`, `cancelledUserId`, `bookingId`, `bookedStatus`)
					SELECT fromTime, toTime, bookedTime, seats, price, totalPrice, TRUNCATE(p_refund, 2), paymentId, paymentMethod, cancellationPolicyId, facilityId, userId, v_userId, id, status FROM booking WHERE id = p_bookingId;
                    IF((SELECT typeName from facility WHERE id = v_facilityTd) = 'Co-Working Space' AND v_userType = v_spTypeId) THEN 
						UPDATE cancellation SET bookedStatus = 4 WHERE bookingId = p_bookingId;
                    END IF;
					SET p_cancel = LAST_INSERT_ID();
					SELECT u.displayName AS spName, u.emailId AS spEmailId, u.mobileNo AS spMobileNo, u.mobileNoVerified AS spMobileVerified, 
							p.displayName AS cName, p.emailId AS cEmailId, p.mobileNo AS cMobileNo, p.mobileNoVerified  AS cMobileVerified,
							c.duration1, c.percentage1, c.duration2, c.percentage2, c.duration3, c.percentage3, b.id AS bookingId, b.fromTime, b.paymentId, TRUNCATE(p_refund, 2) AS totalPrice, f.cityName 
						FROM user_pref u 
						JOIN facility f ON f.spUserId = u.userId 
						JOIN facility_cancellation_charges c ON c.id = f.cancellationPolicyId
						JOIN booking b ON b.facilityId = f.id 
						JOIN user_pref p ON p.userId = b.userId
						WHERE b.id = p_bookingId;
                    DELETE FROM booking WHERE id = p_bookingId;
                    SET p_count = 0;
                END IF;
            END IF;
		ELSEIF((p_type = 2 OR p_type = 3) AND v_userType = v_spTypeId) THEN
            SELECT id INTO v_pendStatus FROM status WHERE name = 'Verify request';
			SELECT id INTO v_approvedStatus FROM status WHERE name = 'Approved and Not Verified';
			SELECT id INTO v_verifiedStatus FROM status WHERE name = 'Approved and Verified';
			SELECT id INTO v_deactivateStatus FROM status WHERE name = 'Deactivated by SP';
			SELECT id INTO v_veriAndDeacStatus FROM status WHERE name = 'Verified And Deactivated by SP';
			SELECT status INTO v_days FROM facility WHERE id = p_facilityId;
            IF(v_days <> v_approvedStatus AND v_days <> v_verifiedStatus AND v_days <> v_pendStatus) THEN
				SET p_result = 14;
			ELSEIF((SELECT spUserId FROM facility WHERE id = p_facilityId) <> v_userId) THEN
				SET p_result = 15;
			ELSE
				IF(p_type = 2) THEN
					SELECT GROUP_CONCAT(id), COUNT(id) INTO v_bookingIds, p_count FROM booking WHERE (
											p_fromDateTime BETWEEN fromTime AND toTime OR 
											p_toDateTime BETWEEN fromTime AND toTime OR 
											fromTime BETWEEN p_fromDateTime AND p_toDateTime) 
											AND facilityId = p_facilityId AND (status = 1 OR status = 3) AND paymentMethod <> 'Offline';
				ELSE
					SELECT GROUP_CONCAT(id), COUNT(id) INTO v_bookingIds, p_count FROM booking WHERE fromTime >= NOW() AND facilityId = p_facilityId AND (status = 1 OR status = 3) AND paymentMethod <> 'Offline';
                END IF;
				IF((p_operation = 3 OR p_operation = 4) AND p_count = 0) THEN
					SET p_result = 16;
					IF(p_type = 3 AND p_operation = 4)THEN
						IF(v_days = v_verifiedStatus) THEN
							UPDATE facility SET status = v_veriAndDeacStatus WHERE id = p_facilityId;
                            INSERT INTO huddil.facility_history(`oldStatus`, `comments`, `facilityId`, `userId`)values(v_days, p_reason, p_facilityId, v_userId);
						ELSE
							UPDATE facility SET status = v_deactivateStatus WHERE id = p_facilityId;
                            INSERT INTO huddil.facility_history(`oldStatus`, `comments`, `facilityId`, `userId`)values(v_days, p_reason, p_facilityId, v_userId);
						END IF;						
                    END IF;
				ELSEIF(p_operation = 3 AND p_count <> 0) THEN
					SET p_result = 17;
					SELECT SUM(totalPrice) INTO p_totalPrice FROM booking WHERE FIND_IN_SET(id, v_bookingIds);
				ELSEIF(p_operation = 4 AND p_count <> 0) THEN
					SET p_result = 18;
					SELECT SUM(totalPrice) INTO p_totalPrice FROM booking WHERE FIND_IN_SET(id, v_bookingIds);
					INSERT INTO `huddil`.`cancellation`
						(`bookedFrom`, `bookedTo`, `bookedTime`, `seats`, `price`, `totalPrice`, `refundAmount`, `paymentId`, `paymentMethod`, `cancellationPolicyId`, `facilityId`, `bookedUserId`, `cancelledUserId`, `bookingId`, `bookedStatus`)
							SELECT fromTime, toTime, bookedTime, seats, price, totalPrice, totalPrice, paymentId, paymentMethod, cancellationPolicyId, facilityId, userId, v_userId, id, status FROM booking WHERE FIND_IN_SET(id, v_bookingIds);
					IF((SELECT typeName from facility WHERE id = p_facilityId) = 'Co-Working Space') THEN 
						UPDATE cancellation SET bookedStatus = 4 WHERE FIND_IN_SET(bookingId, v_bookingIds);
                    END IF;
					IF(p_type = 2) THEN
						INSERT INTO `huddil`.`facility_under_maintenance` (`fromDateTime`, `toDateTime`, `reason`, `facilityId`)
							VALUES (p_fromDateTime, p_toDateTime, p_reason, p_facilityId);
					ELSE
						IF(v_days = v_verifiedStatus) THEN
							UPDATE facility SET status = v_veriAndDeacStatus WHERE id = p_facilityId;
                            INSERT INTO huddil.facility_history(`oldStatus`, `comments`, `facilityId`, `userId`)values(v_days, p_reason, p_facilityId, v_userId);
						ELSE
							UPDATE facility SET status = v_deactivateStatus WHERE id = p_facilityId;
                            INSERT INTO huddil.facility_history(oldStatus, comments, facilityId, userId)values(v_days, p_reason, p_facilityId, v_userId);
                        END IF;
                    END IF;
                    SET p_cancel = LAST_INSERT_ID();
					SELECT u.displayName AS spName, u.emailId AS spEmailId, u.mobileNo AS spMobileNo, u.mobileNoVerified AS spMobileVerified, 
							p.displayName AS cName, p.emailId AS cEmailId, p.mobileNo AS cMobileNo, p.mobileNoVerified  AS cMobileVerified,
							c.duration1, c.percentage1, c.duration2, c.percentage2, c.duration3, c.percentage3, b.id AS bookingId, b.fromTime, b.paymentId, b.totalPrice, f.cityName 
						FROM user_pref u 
						JOIN facility f ON f.spUserId = u.userId 
						JOIN facility_cancellation_charges c ON c.id = f.cancellationPolicyId
						JOIN booking b ON b.facilityId = f.id 
						JOIN user_pref p ON p.userId = b.userId
						WHERE FIND_IN_SET(b.id, v_bookingIds);
					DELETE FROM booking WHERE FIND_IN_SET(id, v_bookingIds);
				END IF;
                SET p_cancellationPrice = p_totalPrice;
            END IF;
		ELSEIF(p_type = 4) THEN
			INSERT INTO `huddil`.`booking`
			(`id`, `fromTime`, `toTime`, `seats`, `price`, `totalPrice`, `cancellationPolicyId`, `paymentMethod`, `paymentId`, `userId`, `facilityId`, `bookedTime`, `status`)
				SELECT bookingId, bookedFrom, bookedTo, seats, price, totalPrice, cancellationPolicyId, paymentMethod, paymentId, bookedUserId, facilityId, bookedTime, bookedStatus FROM cancellation WHERE bookingId = p_bookingId;
			UPDATE booking SET status = 1 WHERE id = p_bookingId;
            DELETE FROM cancellation WHERE bookingId = p_bookingId;
			SET p_result = 19;
            IF(p_operation = 5)THEN
				SELECT oldStatus INTO v_bookingStatus FROM facility_history WHERE facilityId = p_facilityId order by dateTime desc LIMIT 1;
                UPDATE facility SET status = v_bookingStatus WHERE id = p_facilityId;
			END IF;            
        ELSE
			SET p_result = 5;
		END IF;
	END IF;
	SET p_refund = TRUNCATE(p_refund, 2);
	SET p_cancellationPrice = TRUNCATE(p_cancellationPrice, 2);
	SET p_totalPrice = TRUNCATE(p_totalPrice, 2);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `schedulerHelper` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `schedulerHelper`()
BEGIN

    DECLARE v_ids MEDIUMTEXT;
    DECLARE v_spIds MEDIUMTEXT;
    DECLARE v_presentMonth INT;
    DECLARE v_month VARCHAR(90);
    DECLARE v_presentYear INT;

    SELECT group_concat(id) INTO v_ids FROM facility_offers  WHERE endDate < now();
    DELETE from facility_offers where FIND_IN_SET(id,v_ids);    
    IF(DAY(CURDATE()) = 1) THEN
       SET v_presentMonth = MONTH(CURRENT_DATE());
       SET v_presentYear = YEAR(CURRENT_DATE());

       IF(v_presentMonth <> 1) THEN
           SELECT GROUP_CONCAT(spUserId) INTO v_spIds FROM sp_commission WHERE year = v_presentYear AND FIND_IN_SET(v_presentMonth, month);
           IF(v_spIds IS NULL) THEN
               SELECT GROUP_CONCAT(spUserId) INTO v_spIds FROM sp_commission;
           ELSE
               SELECT GROUP_CONCAT(spUserId) INTO v_spIds FROM sp_commission WHERE NOT FIND_IN_SET(spUserId, v_spIds);
           END IF;
     
           UPDATE sp_commission SET month = CONCAT(month, ',', v_presentMonth)
          WHERE year = v_presentYear AND FIND_IN_SET(v_presentMonth - 1, month) AND FIND_IN_SET(spUserId, v_spIds);
       ELSE
           SELECT GROUP_CONCAT(spUserId) INTO v_spIds FROM sp_commission WHERE year = v_presentYear - 1 AND FIND_IN_SET(12, month);
           IF(v_spIds IS NULL) THEN
               SELECT GROUP_CONCAT(spUserId) INTO v_spIds FROM sp_commission;
           ELSE
               SELECT GROUP_CONCAT(spUserId) INTO v_spIds FROM sp_commission WHERE NOT FIND_IN_SET(spUserId, v_spIds);
           END IF;
     
           INSERT INTO `huddil`.`sp_commission`(`spUserId`, `month`, `year`, `commission`)
          SELECT spUserId, 1, v_presentYear, commission FROM sp_commission
           WHERE year = v_presentYear - 1 AND FIND_IN_SET(12, month) AND FIND_IN_SET(spUserId, v_spIds);
       END IF;
   END IF;    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

/*!50003 DROP PROCEDURE IF EXISTS `filterFacilities` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `filterFacilities`(IN v_sessionId VARCHAR(255), IN v_cityId INT, IN v_localityId INT, IN v_locationId INT, IN v_type INT, IN v_search VARCHAR(100), IN v_status INT, IN v_pageNo INT, INOUT v_count INT, OUT v_flag INT)
BEGIN

DECLARE v_city INT;
DECLARE v_locality INT;
DECLARE v_userType INT;
DECLARE v_location INT;
DECLARE v_lowerBound INT;
DECLARE v_totalRecords INT;

SET v_totalRecords = 0;
SET v_lowerBound = (v_pageNo - 1) * v_count;


a_Block:BEGIN
SET v_flag =0;
SELECT p.userType INTO v_userType FROM huddil.user_pref p WHERE p.sessionId = v_sessionId;
IF(v_userType IS NOT NULL)THEN
	IF(v_userType = 7 || v_userType =6)THEN
		
		SET @queryOne = "SELECT COUNT(DISTINCT f.id) INTO @v_totalRecords FROM huddil.facility f JOIN huddil.facility_photo ph ON ph.facilityId = f.id JOIN huddil.user_pref p ON p.userId = f.spUserId JOIN huddil.facility_amenity a ON a.facilityId = f.id JOIN huddil.amenity am ON am.id = a.amenityId";
		SET @query = "SELECT DISTINCT f.id, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailId, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath FROM huddil.facility f JOIN huddil.facility_photo ph ON ph.facilityId = f.id JOIN huddil.user_pref p ON p.userId = f.spUserId JOIN huddil.facility_amenity a ON a.facilityId = f.id JOIN huddil.amenity am ON am.id = a.amenityId";

		IF(v_userType = 6)THEN
			SET @queryOne = REPLACE(@queryOne, ' ON p.userId = f.spUserId','');
			SET @query = REPLACE(@query, ' ON p.userId = f.spUserId','');
		END IF;
		
		IF(v_cityId != 0 OR v_localityId != 0 OR v_locationId != 0)THEN
			SET @queryOne = CONCAT(@queryOne," JOIN huddil.city c ON c.name = f.cityName");
			SET @query = CONCAT(@query," JOIN huddil.city c ON c.name = f.cityName");
			IF(v_cityId !=0)THEN
				SELECT c.id INTO v_city FROM huddil.city c WHERE c.id = v_cityId;
					IF(v_city IS NULL)THEN 
						LEAVE a_Block;
					END IF;
			END IF;
		END IF;
		IF(v_localityId !=0 OR v_locationId != 0)THEN
			IF(v_localityId !=0)THEN
				SELECT l.cityId,l.id INTO v_city,v_locality FROM huddil.locality l WHERE l.id = v_localityId;
				IF(v_locality IS NULL)THEN
					LEAVE a_Block;
				END IF;
				IF(v_cityId !=0)THEN
					IF(v_city != v_cityId)THEN
						LEAVE a_Block;
					 END IF;   
				ELSE
					SET v_cityId = v_city;
				END IF;
			END IF;
			SET @queryOne = CONCAT(@queryOne, " JOIN huddil.locality l ON l.name = f.localityName");
			SET @query = CONCAT(@query, " JOIN huddil.locality l ON l.name = f.localityName");
		END IF;
		IF(v_locationId != 0)THEN
			
			SET @queryOne = CONCAT(@queryOne, " JOIN huddil.location lo ON lo.id = f.locationId");
			SET @query = CONCAT(@query, " JOIN huddil.location lo ON lo.id = f.locationId");
			SELECT lo.cityId, lo.localityId INTO v_city, v_locality FROM huddil.location lo WHERE lo.id = v_locationId;	
			IF(v_cityId !=0)THEN
				IF(v_city != v_cityId)THEN
					LEAVE a_Block;
				END IF;    
			END IF;
			IF(v_localityId !=0)THEN
				IF(v_locality != v_localityId)THEN
					LEAVE a_Block;
				END IF;
			END IF;
					SET v_cityId = v_city;
					SET v_localityId = v_locality;
		ELSEIF(v_locationId =0)THEN
				SET @queryOne = CONCAT(@queryOne, " JOIN huddil.location lo ON lo.id = f.locationId");
				SET @query = CONCAT(@query, " JOIN huddil.location lo ON lo.id = f.locationId");
		END IF;
		IF(v_type != 0)THEN
			
			SET @queryOne = CONCAT(@queryOne, " JOIN huddil.facility_type t ON f.typeName = t.name");
			SET @query = CONCAT(@query, " JOIN huddil.facility_type t ON f.typeName = t.name");
		END IF;
			
			SET @queryOne = CONCAT(@queryOne, ' WHERE c.id = v_cityId AND l.id = v_localityId AND lo.id = v_locationId AND t.id = v_type AND p.sessionId = \'', v_sessionId, '\'','_join AND f.status = v_status');
			SET @query = CONCAT(@query, ' WHERE c.id = v_cityId AND l.id = v_localityId AND lo.id = v_locationId AND t.id = v_type AND p.sessionId = \'', v_sessionId, '\'','_join AND f.status = v_status group by f.id order by f.dateTime desc LIMIT v_lowerBound, v_count');
		IF(v_cityId = 0 && v_localityId = 0 && v_locationId = 0)THEN
			SET @queryOne = REPLACE(@queryOne, 'c.id = v_cityId AND l.id = v_localityId AND lo.id = v_locationId AND', '');
			SET @query = REPLACE(@query, 'c.id = v_cityId AND l.id = v_localityId AND lo.id = v_locationId AND', '');
		ELSEIF((v_cityId =0 && v_localityId !=0 && v_locationId =0) OR (v_cityId != 0 && v_localityId !=0 && v_locationId =0))THEN
			SET @queryOne = REPLACE(@queryOne, ' lo.id = v_locationId AND','');
			SET @query = REPLACE(@query, ' lo.id = v_locationId AND','');
		ELSEIF(v_cityId != 0 && v_localityId =0 && v_locationId = 0)THEN
			SET @queryOne = REPLACE(@queryOne, 'AND l.id = v_localityId AND lo.id = v_locationId',''); 
			SET @query = REPLACE(@query, 'AND l.id = v_localityId AND lo.id = v_locationId',''); 
		END IF;
		IF(v_type = 0)THEN
			SET @queryOne = REPLACE(@queryOne, 't.id = v_type AND', '');
			SET @query = REPLACE(@query, 't.id = v_type AND', '');
		ELSE
			SET @queryOne = REPLACE(@queryOne, "v_type", v_type);
			SET @query = REPLACE(@query, "v_type", v_type);
		END IF;
		IF(v_search <> "null") THEN
			SET @queryOne = REPLACE(@queryOne, '_join', CONCAT(' AND f.title like ''%', v_search, '%'''));
			SET @query = REPLACE(@query, '_join', CONCAT(' AND f.title like ''%', v_search, '%'''));
		ELSE
			SET @queryOne = REPLACE(@queryOne, '_join','');
			SET @query = REPLACE(@query, '_join','');
		END IF;
		IF(v_status = 0)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND f.status > -1');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND f.status > -1');
		ELSEIF(v_status = -1 OR v_status = -2)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND (f.status = -1 OR f.status = -2)');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND (f.status = -1 OR f.status = -2)');
		ELSEIF(v_status = 1)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND (f.status = 1 OR  f.status = 2)');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND (f.status = 1 OR  f.status = 2)');
		ELSEIF(v_status = 2)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND (f.status = 3 OR  f.status = 4)');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND (f.status = 3 OR  f.status = 4)');
		ELSEIF(v_status = 3)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND (f.status = 5 OR  f.status = 6 OR f.status = 7 OR f.status = 8)');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND (f.status = 5 OR  f.status = 6 OR f.status = 7 OR f.status = 8)');
		ELSEIF(v_status = 4)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND (f.status = 9 OR  f.status = 10 OR f.status = 11 OR f.status = 12 OR f.status = 13 OR f.status = 14)');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND (f.status = 9 OR  f.status = 10 OR f.status = 11 OR f.status = 12 OR f.status = 13 OR f.status = 14)');
		ELSEIF(v_status = 5)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND f.status = 5');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND f.status = 5');
        ELSEIF(v_status = 6)THEN
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', ' AND f.status = 8');
			SET @query = REPLACE(@query, ' AND f.status = v_status', ' AND f.status = 8');
		ELSE
			SET @queryOne = REPLACE(@queryOne, ' AND f.status = v_status', '');
			SET @query = REPLACE(@query, ' AND f.status = v_status', '');
		END IF;
		
		SET @query = REPLACE(@query, "v_cityId", v_cityId);
		SET @query = REPLACE(@query, "v_localityId", v_localityId);
		SET @query = REPLACE(@query, "v_locationId", v_locationId);
		SET @query = REPLACE(@query, "v_lowerBound", v_lowerBound);
		SET @query = REPLACE(@query, "v_count", v_count);
		
		SET @queryOne = REPLACE(@queryOne, "v_cityId", v_cityId);
		SET @queryOne = REPLACE(@queryOne, "v_localityId", v_localityId);
		SET @queryOne = REPLACE(@queryOne, "v_locationId", v_locationId);

		PREPARE stmt FROM @query;
		EXECUTE stmt;
		PREPARE stmt FROM @queryOne;
		EXECUTE stmt;
		SET v_flag = 1;
		SET v_count = @v_totalRecords;
		
	ELSE
		SET v_flag = -2;
	END IF;
ELSE
	SET v_flag = -1;
END IF;
END a_Block;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;             
/*!50003 DROP PROCEDURE IF EXISTS `adminFacility` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `adminFacility`(IN p_sessionId VARCHAR(90), IN p_search VARCHAR(90), IN p_searchType VARCHAR(50), IN p_facilityType INT, IN p_pageNo INT, INOUT p_count INT, OUT p_result INT)
BEGIN

DECLARE v_lowerBound INT;
DECLARE v_totalRecords INT;
DECLARE v_userType INT;

    SELECT userType INTO v_userType FROM user_pref WHERE sessionId = p_sessionId;
	 SET p_result = 0;
    IF(v_userType IS NULL) THEN
		SET p_result = -1;
	ELSEIF(v_userType <> (SELECT id FROM user_type WHERE name = 'Administrator')) THEN
		SET p_result = -2;
	ELSE

	SET v_lowerBound = (p_pageNo - 1) * p_count;
	SET v_totalRecords = 0;




SET @query = "SELECT DISTINCT f.id, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, 
    f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailid, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, 
    f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, 
    GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath FROM huddil.facility f 
		JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
        JOIN huddil.facility_amenity a ON a.facilityId = f.id 
        JOIN huddil.amenity am ON am.id = a.amenityId 
        JOIN huddil.city c ON c.name = f.cityName 
        JOIN huddil.locality l ON l.name = f.localityName 
        JOIN huddil.location lo ON lo.id = f.locationId 
        JOIN huddil.facility_type t ON t.name = f.typeName";
SET @queryOne = "SELECT COUNT(DISTINCT f.id) INTO @v_totalRecords FROM huddil.facility f 
		JOIN huddil.facility_photo ph ON ph.facilityId = f.id 
        JOIN huddil.facility_amenity a ON a.facilityId = f.id 
        JOIN huddil.amenity am ON am.id = a.amenityId 
        JOIN huddil.city c ON c.name = f.cityName 
        JOIN huddil.locality l ON l.name = f.localityName 
        JOIN huddil.location lo ON lo.id = f.locationId 
        JOIN huddil.facility_type t ON t.name = f.typeName";

IF (p_searchType = 'locality')THEN
	
    SET @query = CONCAT(@query , ' WHERE f.localityName LIKE ''%', p_search ,'%'' AND t.id = ',p_facilityType,' AND f.status > -1 group by f.id LIMIT v_lowerBound, p_count');
	SET @queryOne = CONCAT(@queryOne , ' WHERE f.localityName LIKE ''%', p_search ,'%'' AND t.id = ',p_facilityType, ' AND f.status > -1');

ELSEIF (p_searchType = 'city')THEN

	SET @query = CONCAT(@query, ' WHERE f.cityName LIKE ''%', p_search ,'%'' AND t.id = ',p_facilityType,' AND f.status > -1 group by f.id LIMIT v_lowerBound, p_count');
    SET @queryOne = CONCAT(@queryOne, ' WHERE f.cityName LIKE ''%', p_search ,'%'' AND t.id = ',p_facilityType, '  AND f.status > -1 ');

ELSEIF(p_searchType = 'service provider')THEN
	
    SET @query = CONCAT(@query, ' JOIN huddil.user_pref p ON p.userId = f.spUserId WHERE p.displayName LIKE ''%', p_search ,'%'' AND t.id = ',p_facilityType,' AND f.status > -1 group by f.id LIMIT v_lowerBound, p_count');
    SET @queryOne = CONCAT(@queryOne, ' JOIN huddil.user_pref p ON p.userId = f.spUserId WHERE p.displayName LIKE ''%', p_search ,'%'' AND t.id = ',p_facilityType, ' AND f.status > -1');
			
END IF;

	SET @query = REPLACE(@query, 'v_lowerBound', v_lowerBound);
    SET @query = REPLACE(@query, 'p_count', p_count);

	PREPARE stmt FROM @query;
    EXECUTE stmt;
    
    PREPARE stmt FROm @queryOne;
    EXECUTE stmt;
	
    SET p_count = @v_totalRecords;
END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;	