-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 31, 2026 at 06:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `crms_db`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetCrimeRecordsByStation` (IN `p_station_id` INT)   BEGIN
    SELECT cr.*
    FROM crime_records cr
    JOIN fir f ON cr.fir_number = f.fir_number
    WHERE f.station_id = p_station_id
      AND cr.is_active = 1
    ORDER BY cr.id DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetUnassignedFIRsByStation` (IN `p_station_id` INT)   BEGIN
    SELECT * 
    FROM fir 
    WHERE station_id = p_station_id 
      AND assigned_officer_id IS NULL 
      AND is_active = 1
    ORDER BY fir_number;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetUsersByStation` (IN `p_station_id` INT, IN `p_role` VARCHAR(20), IN `p_active_only` TINYINT)   BEGIN
    SELECT id, username, password, role, station_id, full_name, active, email
    FROM users
    WHERE station_id = p_station_id
      AND (p_role IS NULL OR role = p_role)
      AND (p_active_only = 0 OR active = 1)
    ORDER BY username;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetVictimsByStation` (IN `p_station_id` INT)   BEGIN
    SELECT v.*
    FROM victims v
    JOIN fir f ON v.fir_number = f.fir_number
    WHERE f.station_id = p_station_id
      AND v.is_active = 1
      AND f.is_active = 1
    ORDER BY v.id DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `IsFIRAtStation` (IN `p_fir_number` VARCHAR(50), IN `p_station_id` INT, OUT `p_is_at_station` TINYINT)   BEGIN
    SELECT COUNT(*) > 0 INTO p_is_at_station
    FROM fir
    WHERE fir_number = p_fir_number
      AND station_id = p_station_id
      AND is_active = 1;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `designation` varchar(100) DEFAULT 'Administrator',
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp(),
  `is_super_admin` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`id`, `user_id`, `designation`, `is_active`, `created_at`, `is_super_admin`) VALUES
(1, 1, 'Station Admin', 1, '2026-08-04 22:13:56', 0),
(2, 4, 'Super Admin', 1, '2026-08-04 23:11:05', 1),
(3, 7, 'Station Admin', 1, '2026-08-05 03:04:34', 0),
(5, 15, 'Station Admin', 1, '2026-08-08 11:54:10', 0),
(6, 16, 'Station Admin', 1, '2026-08-08 12:03:04', 0);

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `action` varchar(100) DEFAULT NULL,
  `table_name` varchar(50) DEFAULT NULL,
  `record_id` varchar(50) DEFAULT NULL,
  `old_value` text DEFAULT NULL,
  `new_value` text DEFAULT NULL,
  `timestamp` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `audit_logs`
--

INSERT INTO `audit_logs` (`id`, `user_id`, `username`, `action`, `table_name`, `record_id`, `old_value`, `new_value`, `timestamp`) VALUES
(1, NULL, NULL, 'DELETE', 'victims', '1', 'ID                  : 1\nFIR Number          : FIR-CPS-1\nFirst Name          : Tarak\nLast Name           : Patel\nActive              : Yes', 'DELETED', '2026-08-04 08:33:29'),
(2, NULL, NULL, 'DELETE', 'victims', '2', 'ID                  : 2\nFIR Number          : FIR-CPS-3\nFirst Name          : Loki\nLast Name           : Kumar\nActive              : Yes', 'DELETED', '2026-08-04 08:33:29'),
(3, NULL, NULL, 'DELETE', 'victims', '3', 'ID                  : 3\nFIR Number          : FIR-CM1-5\nFirst Name          : Messi\nLast Name           : Lionel\nActive              : Yes', 'DELETED', '2026-08-04 08:33:29'),
(4, NULL, NULL, 'DELETE', 'witnesses', '1', 'ID                  : 1\nFIR Number          : FIR-CPS-1\nFirst Name          : Raj\nLast Name           : Purohit\nActive              : Yes', 'DELETED', '2026-08-04 08:33:29'),
(5, NULL, NULL, 'DELETE', 'witnesses', '2', 'ID                  : 2\nFIR Number          : FIR-CPS-4\nFirst Name          : Sita\nLast Name           : Sharma\nActive              : Yes', 'DELETED', '2026-08-04 08:33:29'),
(6, NULL, NULL, 'DELETE', 'witnesses', '3', 'ID                  : 3\nFIR Number          : FIR-CM1-6\nFirst Name          : Draco\nLast Name           : Malfoy\nActive              : Yes', 'DELETED', '2026-08-04 08:33:29'),
(7, NULL, NULL, 'INSERT', 'police_stations', 'CENTRAL AHMEDABAD 1', NULL, 'ID                  : 1\nStation Code        : CA1\nStation Name        : CENTRAL AHMEDABAD 1\nAddress             : lal darvaja,ahmedabad\nPhone               : 9999999999\nActive              : Yes', '2026-08-04 08:36:45'),
(8, NULL, NULL, 'INSERT', 'users', 'admin', NULL, 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:37:40'),
(9, 1, 'admin', 'INSERT', 'users', 'inspector_raj', NULL, 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:38:51'),
(10, 1, 'admin', 'INSERT', 'officers', NULL, NULL, 'ID                  : 1\nUser ID             : 2\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:38:56'),
(11, 1, 'admin', 'UPDATE', 'officers', '202600001', 'ID                  : 1\nUser ID             : 2\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:38:56'),
(12, 1, 'admin', 'INSERT', 'users', 'inspector_raj', NULL, 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:49:42'),
(13, 1, 'admin', 'INSERT', 'officers', NULL, NULL, 'ID                  : 1\nUser ID             : 2\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:49:45'),
(14, 1, 'admin', 'UPDATE', 'officers', '202600001', 'ID                  : 1\nUser ID             : 2\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:49:45'),
(15, 1, 'admin', 'INSERT', 'users', 'inspector_raj', NULL, 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:51:29'),
(16, 1, 'admin', 'INSERT', 'officers', NULL, NULL, 'ID                  : 2\nUser ID             : 2\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:51:31'),
(17, 1, 'admin', 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 2\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 2\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:51:31'),
(18, 1, 'admin', 'UPDATE', 'users', 'inspector_raj', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:51:31'),
(19, NULL, NULL, 'UPDATE', 'officers', '202600001', 'ID                  : 2\nUser ID             : 2\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:52:01'),
(20, NULL, NULL, 'UPDATE', 'officers', '202600001', 'ID                  : 2\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:52:07'),
(21, NULL, NULL, 'UPDATE', 'users', 'admin', 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:52:57'),
(22, NULL, NULL, 'UPDATE', 'users', 'inspector_raj', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', '2026-08-04 08:53:15'),
(23, 1, 'admin', 'INSERT', 'users', 'officer_rahul', NULL, 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul marwadi\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:17:47'),
(24, 1, 'admin', 'INSERT', 'officers', NULL, NULL, 'ID                  : 2\nUser ID             : 3\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:17:52'),
(25, 1, 'admin', 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:17:52'),
(27, 1, 'admin', 'INSERT', 'users', 'officer_rahul', NULL, 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:29:30'),
(28, 1, 'admin', 'INSERT', 'officers', NULL, NULL, 'ID                  : 2\nUser ID             : 3\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:29:33'),
(29, NULL, NULL, 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:29:33'),
(30, NULL, NULL, 'UPDATE', 'users', 'officer_rahul', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:29:33'),
(31, NULL, NULL, 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:29:33'),
(32, 1, 'admin', 'UPDATE', 'users', 'admin', 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:31:13'),
(33, 1, 'admin', 'UPDATE', 'officers', '202600001', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:41:10'),
(34, 1, 'admin', 'UPDATE', 'users', 'inspector_raj', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap mahol\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:41:10'),
(35, 1, 'admin', 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : No', '2026-08-04 22:49:25'),
(36, 1, 'admin', 'UPDATE', 'users', 'officer_rahul', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : No', '2026-08-04 22:49:25'),
(37, 1, 'admin', 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : No', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:49:31'),
(38, 1, 'admin', 'UPDATE', 'users', 'officer_rahul', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : No', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', '2026-08-04 22:49:31'),
(39, NULL, NULL, 'INSERT', 'police_stations', 'Central Gujarat', NULL, 'ID                  : 2\nStation Code        : CG\nStation Name        : Central Gujarat\nAddress             : patan,gujarat\nPhone               : 9547563215\nActive              : Yes', '2026-08-04 23:08:16'),
(40, NULL, NULL, 'INSERT', 'users', 'Super', NULL, 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : Yes', '2026-08-04 23:09:36'),
(41, NULL, NULL, 'UPDATE', 'users', 'Super', 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : Yes', 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : Yes', '2026-08-04 23:10:03'),
(42, 4, 'Super', 'INSERT', 'users', 'officer_aman', NULL, 'ID                  : 5\nUsername            : officer_aman\nRole                : OFFICER\nFull Name           : aman yadav\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:23:04'),
(43, 4, 'Super', 'INSERT', 'officers', NULL, NULL, 'ID                  : 3\nUser ID             : 5\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:23:06'),
(44, NULL, NULL, 'UPDATE', 'officers', '202600003', 'ID                  : 3\nUser ID             : 5\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 3\nUser ID             : 5\nBadge Number        : 202600003\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:23:06'),
(45, NULL, NULL, 'UPDATE', 'officers', '202600003', 'ID                  : 3\nUser ID             : 5\nBadge Number        : 202600003\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 3\nUser ID             : 5\nBadge Number        : 202600003\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:23:06'),
(46, NULL, NULL, 'UPDATE', 'users', 'officer_aman', 'ID                  : 5\nUsername            : officer_aman\nRole                : OFFICER\nFull Name           : aman yadav\nStation ID          : 1\nActive              : Yes', 'ID                  : 5\nUsername            : officer_aman\nRole                : OFFICER\nFull Name           : aman yadav\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:23:06'),
(47, 4, 'Super', 'INSERT', 'users', 'staff_arham', NULL, 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:32:09'),
(48, 4, 'Super', 'INSERT', 'staff', NULL, NULL, 'ID                  : 1\nUser ID             : 6\nEmployee ID         : N/A\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', '2026-08-04 23:32:09'),
(49, NULL, NULL, 'UPDATE', 'staff', '202600001', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : N/A\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', '2026-08-04 23:32:09'),
(50, NULL, NULL, 'UPDATE', 'staff', '202600001', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', '2026-08-04 23:32:09'),
(51, NULL, NULL, 'UPDATE', 'users', 'staff_arham', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', '2026-08-04 23:32:09'),
(52, 4, 'Super', 'INSERT', 'police_stations', 'kheda police station', NULL, 'ID                  : 3\nStation Code        : KPS\nStation Name        : kheda police station\nAddress             : kheda\nPhone               : 9874563546\nActive              : Yes', '2026-08-04 23:33:04'),
(53, 6, 'staff_arham', 'INSERT', 'fir', NULL, NULL, 'ID                  : 1\nFIR Number          : N/A\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 1\nAssigned Officer    : None\nActive              : Yes', '2026-08-04 23:38:05'),
(54, 6, 'staff_arham', 'UPDATE', 'fir', 'FIR-CA1-1', 'ID                  : 1\nFIR Number          : N/A\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 1\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 1\nAssigned Officer    : None\nActive              : Yes', '2026-08-04 23:38:05'),
(55, 6, 'staff_arham', 'INSERT', 'victims', '1', NULL, 'ID                  : 1\nFIR Number          : FIR-CA1-1\nFirst Name          : raju\nLast Name           : taklu\nActive              : Yes', '2026-08-04 23:45:56'),
(56, 6, 'staff_arham', 'INSERT', 'witnesses', '1', NULL, 'ID                  : 1\nFIR Number          : FIR-CA1-1\nFirst Name          : jaggu\nLast Name           : bandar\nActive              : Yes', '2026-08-04 23:52:40'),
(57, 6, 'staff_arham', 'UPDATE', 'fir', 'FIR-CA1-1', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 1\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : None\nActive              : Yes', '2026-08-04 23:57:20'),
(58, 1, 'admin', 'UPDATE', 'fir', 'FIR-CA1-1', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : ASSIGNED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : 2\nActive              : Yes', '2026-08-05 00:31:45'),
(59, 3, 'officer_rahul', 'UPDATE', 'fir', 'FIR-CA1-1', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : ASSIGNED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : 2\nActive              : Yes', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : 2\nActive              : Yes', '2026-08-05 00:32:50'),
(60, 3, 'officer_rahul', 'INSERT', 'investigations', 'FIR-CA1-1', NULL, 'ID                  : 1\nFIR Number          : FIR-CA1-1\nOfficer ID          : 2\nStatus              : OPEN\nActive              : Yes', '2026-08-05 00:33:07'),
(61, 3, 'officer_rahul', 'INSERT', 'evidence', NULL, NULL, 'ID                  : 2\nEvidence Number     : N/A\nFIR Number          : FIR-CA1-1\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 00:47:45'),
(62, 3, 'officer_rahul', 'UPDATE', 'evidence', 'EV-FIR-CA1-1-2', 'ID                  : 2\nEvidence Number     : N/A\nFIR Number          : FIR-CA1-1\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', 'ID                  : 2\nEvidence Number     : EV-FIR-CA1-1-2\nFIR Number          : FIR-CA1-1\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 00:47:45'),
(63, 3, 'officer_rahul', 'UPDATE', 'investigations', 'FIR-CA1-1', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nOfficer ID          : 2\nStatus              : OPEN\nActive              : Yes', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nOfficer ID          : 2\nStatus              : OPEN\nActive              : Yes', '2026-08-05 00:48:51'),
(64, 3, 'officer_rahul', 'INSERT', 'criminals', '1', NULL, 'ID                  : 1\nFirst Name          : harshad\nLast Name           : metha\nDOB                 : 1940-05-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-05 00:49:47'),
(65, 3, 'officer_rahul', 'UPDATE', 'criminals', '1', 'ID                  : 1\nFirst Name          : harshad\nLast Name           : metha\nDOB                 : 1940-05-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', 'ID                  : 1\nFirst Name          : harshad\nLast Name           : metha\nDOB                 : 1940-05-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-05 00:53:02'),
(66, 3, 'officer_rahul', 'INSERT', 'crime_records', NULL, NULL, 'ID                  : 1\nCrime Number        : N/A\nFIR Number          : FIR-CA1-1\nCrime Name          : drunk and drive\nStatus              : ACTIVE\nActive              : Yes', '2026-08-05 01:03:11'),
(67, 3, 'officer_rahul', 'UPDATE', 'crime_records', 'CN-CA1-2026-1', 'ID                  : 1\nCrime Number        : N/A\nFIR Number          : FIR-CA1-1\nCrime Name          : drunk and drive\nStatus              : ACTIVE\nActive              : Yes', 'ID                  : 1\nCrime Number        : CN-CA1-2026-1\nFIR Number          : FIR-CA1-1\nCrime Name          : drunk and drive\nStatus              : ACTIVE\nActive              : Yes', '2026-08-05 01:03:11'),
(68, 3, 'officer_rahul', 'LINK_CRIMINAL_CRIME', 'crime_criminal_mapping', 'CN-CA1-2026-1-1', NULL, 'role=PERPETRATOR', '2026-08-05 01:22:40'),
(69, 3, 'officer_rahul', 'UPDATE', 'evidence', 'EV-FIR-CA1-1-2', 'ID                  : 2\nEvidence Number     : EV-FIR-CA1-1-2\nFIR Number          : FIR-CA1-1\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', 'ID                  : 2\nEvidence Number     : EV-FIR-CA1-1-2\nFIR Number          : FIR-CA1-1\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 01:43:42'),
(70, 3, 'officer_rahul', 'INSERT', 'victims', '2', NULL, 'ID                  : 2\nFIR Number          : FIR-CA1-1\nFirst Name          : jaggu\nLast Name           : bandar\nActive              : Yes', '2026-08-05 01:49:31'),
(71, 4, 'Super', 'INSERT', 'users', 'admin_2', NULL, 'ID                  : 7\nUsername            : admin_2\nRole                : ADMIN\nFull Name           : admin two\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:04:31'),
(72, 7, 'admin_2', 'INSERT', 'users', 'officer_john', NULL, 'ID                  : 8\nUsername            : officer_john\nRole                : OFFICER\nFull Name           : John Doe\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:09:31'),
(73, 7, 'admin_2', 'INSERT', 'officers', NULL, NULL, 'ID                  : 4\nUser ID             : 8\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:09:33'),
(74, NULL, NULL, 'UPDATE', 'officers', '202600004', 'ID                  : 4\nUser ID             : 8\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', 'ID                  : 4\nUser ID             : 8\nBadge Number        : 202600004\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:09:33'),
(75, NULL, NULL, 'UPDATE', 'officers', '202600004', 'ID                  : 4\nUser ID             : 8\nBadge Number        : 202600004\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', 'ID                  : 4\nUser ID             : 8\nBadge Number        : 202600004\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:09:33'),
(76, NULL, NULL, 'UPDATE', 'users', 'officer_john', 'ID                  : 8\nUsername            : officer_john\nRole                : OFFICER\nFull Name           : John Doe\nStation ID          : 3\nActive              : Yes', 'ID                  : 8\nUsername            : officer_john\nRole                : OFFICER\nFull Name           : John Doe\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:09:33'),
(77, 7, 'admin_2', 'INSERT', 'users', 'officer_smith', NULL, 'ID                  : 9\nUsername            : officer_smith\nRole                : OFFICER\nFull Name           : Jane Smith\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:10:38'),
(78, 7, 'admin_2', 'INSERT', 'officers', NULL, NULL, 'ID                  : 5\nUser ID             : 9\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:10:41'),
(79, NULL, NULL, 'UPDATE', 'officers', '202600005', 'ID                  : 5\nUser ID             : 9\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', 'ID                  : 5\nUser ID             : 9\nBadge Number        : 202600005\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:10:41'),
(80, NULL, NULL, 'UPDATE', 'officers', '202600005', 'ID                  : 5\nUser ID             : 9\nBadge Number        : 202600005\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', 'ID                  : 5\nUser ID             : 9\nBadge Number        : 202600005\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:10:41'),
(81, NULL, NULL, 'UPDATE', 'users', 'officer_smith', 'ID                  : 9\nUsername            : officer_smith\nRole                : OFFICER\nFull Name           : Jane Smith\nStation ID          : 3\nActive              : Yes', 'ID                  : 9\nUsername            : officer_smith\nRole                : OFFICER\nFull Name           : Jane Smith\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:10:41'),
(82, 7, 'admin_2', 'INSERT', 'users', 'staff_alice', NULL, 'ID                  : 10\nUsername            : staff_alice\nRole                : STAFF\nFull Name           : alice\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:11:10'),
(83, 7, 'admin_2', 'INSERT', 'staff', NULL, NULL, 'ID                  : 2\nUser ID             : 10\nEmployee ID         : N/A\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', '2026-08-05 03:11:10'),
(84, NULL, NULL, 'UPDATE', 'staff', '202600002', 'ID                  : 2\nUser ID             : 10\nEmployee ID         : N/A\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', 'ID                  : 2\nUser ID             : 10\nEmployee ID         : 202600002\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', '2026-08-05 03:11:10'),
(85, NULL, NULL, 'UPDATE', 'staff', '202600002', 'ID                  : 2\nUser ID             : 10\nEmployee ID         : 202600002\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', 'ID                  : 2\nUser ID             : 10\nEmployee ID         : 202600002\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', '2026-08-05 03:11:10'),
(86, NULL, NULL, 'UPDATE', 'users', 'staff_alice', 'ID                  : 10\nUsername            : staff_alice\nRole                : STAFF\nFull Name           : alice\nStation ID          : 3\nActive              : Yes', 'ID                  : 10\nUsername            : staff_alice\nRole                : STAFF\nFull Name           : alice\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:11:10'),
(87, 7, 'admin_2', 'INSERT', 'users', 'staff_bob', NULL, 'ID                  : 11\nUsername            : staff_bob\nRole                : STAFF\nFull Name           : bob\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:11:27'),
(88, 7, 'admin_2', 'INSERT', 'staff', NULL, NULL, 'ID                  : 3\nUser ID             : 11\nEmployee ID         : N/A\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', '2026-08-05 03:11:27'),
(89, NULL, NULL, 'UPDATE', 'staff', '202600003', 'ID                  : 3\nUser ID             : 11\nEmployee ID         : N/A\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', 'ID                  : 3\nUser ID             : 11\nEmployee ID         : 202600003\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', '2026-08-05 03:11:27'),
(90, NULL, NULL, 'UPDATE', 'staff', '202600003', 'ID                  : 3\nUser ID             : 11\nEmployee ID         : 202600003\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', 'ID                  : 3\nUser ID             : 11\nEmployee ID         : 202600003\nStation ID          : 3\nStation Code        : KPS\nActive              : Yes', '2026-08-05 03:11:27'),
(91, NULL, NULL, 'UPDATE', 'users', 'staff_bob', 'ID                  : 11\nUsername            : staff_bob\nRole                : STAFF\nFull Name           : bob\nStation ID          : 3\nActive              : Yes', 'ID                  : 11\nUsername            : staff_bob\nRole                : STAFF\nFull Name           : bob\nStation ID          : 3\nActive              : Yes', '2026-08-05 03:11:27'),
(92, 8, 'officer_john', 'INSERT', 'criminals', '2', NULL, 'ID                  : 2\nFirst Name          : jethalal\nLast Name           : gada\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-05 08:53:05'),
(93, 8, 'officer_john', 'UPDATE', 'criminals', '2', 'ID                  : 2\nFirst Name          : jethalal\nLast Name           : gada\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', 'ID                  : 2\nFirst Name          : jethalal\nLast Name           : gada\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-05 08:53:43'),
(94, 11, 'staff_bob', 'INSERT', 'fir', NULL, NULL, 'ID                  : 2\nFIR Number          : N/A\nComplainant         : grisha\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-05 09:05:25'),
(95, 11, 'staff_bob', 'UPDATE', 'fir', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : N/A\nComplainant         : grisha\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-05 09:05:25'),
(96, 11, 'staff_bob', 'INSERT', 'victims', '3', NULL, 'ID                  : 3\nFIR Number          : FIR-KPS-2\nFirst Name          : shivam\nLast Name           : thakkar\nActive              : Yes', '2026-08-05 09:07:05'),
(97, 11, 'staff_bob', 'INSERT', 'witnesses', '2', NULL, 'ID                  : 2\nFIR Number          : FIR-KPS-2\nFirst Name          : parth\nLast Name           : patel\nActive              : Yes', '2026-08-05 09:07:35'),
(98, 7, 'admin_2', 'UPDATE', 'fir', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : ASSIGNED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', '2026-08-05 09:16:06'),
(99, 8, 'officer_john', 'INSERT', 'investigations', 'FIR-KPS-2', NULL, 'ID                  : 2\nFIR Number          : FIR-KPS-2\nOfficer ID          : 4\nStatus              : OPEN\nActive              : Yes', '2026-08-05 09:16:30'),
(100, 8, 'officer_john', 'INSERT', 'evidence', NULL, NULL, 'ID                  : 5\nEvidence Number     : N/A\nFIR Number          : FIR-KPS-2\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 09:27:43'),
(101, 8, 'officer_john', 'UPDATE', 'evidence', 'EV-FIR-KPS-2-5', 'ID                  : 5\nEvidence Number     : N/A\nFIR Number          : FIR-KPS-2\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', 'ID                  : 5\nEvidence Number     : EV-FIR-KPS-2-5\nFIR Number          : FIR-KPS-2\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 09:27:43'),
(102, 8, 'officer_john', 'UPDATE', 'evidence', 'EV-FIR-KPS-2-5', 'ID                  : 5\nEvidence Number     : EV-FIR-KPS-2-5\nFIR Number          : FIR-KPS-2\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', 'ID                  : 5\nEvidence Number     : EV-FIR-KPS-2-5\nFIR Number          : FIR-KPS-2\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 09:40:43'),
(103, 4, 'Super', 'UPDATE', 'users', 'Super', 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : Yes', 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : No', '2026-08-05 10:18:03'),
(104, 4, 'Super', 'UPDATE', 'users', 'Super', 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : No', 'ID                  : 4\nUsername            : Super\nRole                : ADMIN\nFull Name           : Super Admin\nStation ID          : 2\nActive              : Yes', '2026-08-05 10:19:35'),
(105, 8, 'officer_john', 'UPDATE', 'fir', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : ASSIGNED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', '2026-08-05 10:21:12'),
(106, 11, 'staff_bob', 'INSERT', 'fir', NULL, NULL, 'ID                  : 3\nFIR Number          : N/A\nComplainant         : trial\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-05 10:23:53'),
(107, 11, 'staff_bob', 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : N/A\nComplainant         : trial\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-05 10:23:53'),
(108, 11, 'staff_bob', 'INSERT', 'victims', '4', NULL, 'ID                  : 4\nFIR Number          : FIR-KPS-3\nFirst Name          : trial\nLast Name           : triall\nActive              : Yes', '2026-08-05 10:25:09'),
(109, 1, 'admin', 'INSERT', 'users', 'fG_vhvgk', NULL, 'ID                  : 12\nUsername            : fG_vhvgk\nRole                : OFFICER\nFull Name           : fd hgs\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:23:26'),
(110, 1, 'admin', 'INSERT', 'officers', NULL, NULL, 'ID                  : 6\nUser ID             : 12\nBadge Number        : N/A\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:23:31'),
(111, NULL, NULL, 'UPDATE', 'officers', '202600006', 'ID                  : 6\nUser ID             : 12\nBadge Number        : N/A\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', 'ID                  : 6\nUser ID             : 12\nBadge Number        : 202600006\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:23:31'),
(112, NULL, NULL, 'UPDATE', 'officers', '202600006', 'ID                  : 6\nUser ID             : 12\nBadge Number        : 202600006\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', 'ID                  : 6\nUser ID             : 12\nBadge Number        : 202600006\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:23:31'),
(113, NULL, NULL, 'UPDATE', 'users', 'fG_vhvgk', 'ID                  : 12\nUsername            : fG_vhvgk\nRole                : OFFICER\nFull Name           : fd hgs\nStation ID          : 1\nActive              : Yes', 'ID                  : 12\nUsername            : fG_vhvgk\nRole                : OFFICER\nFull Name           : fd hgs\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:23:31'),
(114, 1, 'admin', 'UPDATE', 'officers', '202600006', 'ID                  : 6\nUser ID             : 12\nBadge Number        : 202600006\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', 'ID                  : 6\nUser ID             : 12\nBadge Number        : 202600006\nRank                : CONSTABLE\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:25:57'),
(115, 1, 'admin', 'UPDATE', 'users', 'trial', 'ID                  : 12\nUsername            : fG_vhvgk\nRole                : OFFICER\nFull Name           : fd hgs\nStation ID          : 1\nActive              : Yes', 'ID                  : 12\nUsername            : trial\nRole                : OFFICER\nFull Name           : trial\nStation ID          : 1\nActive              : Yes', '2026-08-05 11:25:57'),
(116, 1, 'admin', 'UPDATE', 'staff', '202600001', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : No', '2026-08-05 11:26:22'),
(117, 1, 'admin', 'UPDATE', 'users', 'staff_arham', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : No', '2026-08-05 11:26:22'),
(118, 1, 'admin', 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-05 11:28:35'),
(119, 9, 'officer_smith', 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-05 11:30:26'),
(120, 9, 'officer_smith', 'INSERT', 'investigations', 'FIR-KPS-3', NULL, 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-05 11:32:31'),
(121, 9, 'officer_smith', 'UPDATE', 'investigations', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-05 11:32:40'),
(122, 9, 'officer_smith', 'INSERT', 'criminals', '3', NULL, 'ID                  : 3\nFirst Name          : fd\nLast Name           : jhk\nDOB                 : 2008-02-02\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-05 11:33:23'),
(123, 9, 'officer_smith', 'UPDATE', 'criminals', '3', 'ID                  : 3\nFirst Name          : fd\nLast Name           : jhk\nDOB                 : 2008-02-02\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', 'ID                  : 3\nFirst Name          : fd\nLast Name           : jhk\nDOB                 : 2008-02-02\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-05 11:34:34'),
(124, 9, 'officer_smith', 'INSERT', 'evidence', NULL, NULL, 'ID                  : 6\nEvidence Number     : N/A\nFIR Number          : FIR-KPS-3\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 11:38:44'),
(125, 9, 'officer_smith', 'UPDATE', 'evidence', 'EV-FIR-KPS-3-6', 'ID                  : 6\nEvidence Number     : N/A\nFIR Number          : FIR-KPS-3\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', 'ID                  : 6\nEvidence Number     : EV-FIR-KPS-3-6\nFIR Number          : FIR-KPS-3\nCrime Number        : N/A\nType                : IMAGE\nCustodian           : \nActive              : Yes', '2026-08-05 11:38:44'),
(126, 9, 'officer_smith', 'INSERT', 'victims', '5', NULL, 'ID                  : 5\nFIR Number          : FIR-KPS-3\nFirst Name          : fgh\nLast Name           : sdfg\nActive              : Yes', '2026-08-05 11:39:41'),
(127, 11, 'staff_bob', 'INSERT', 'fir', NULL, NULL, 'ID                  : 4\nFIR Number          : N/A\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-07 05:58:42'),
(128, 11, 'staff_bob', 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : N/A\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-07 05:58:42'),
(129, 4, 'Super', 'UPDATE', 'users', 'admin_2', 'ID                  : 7\nUsername            : admin_2\nRole                : ADMIN\nFull Name           : admin two\nStation ID          : 3\nActive              : Yes', 'ID                  : 7\nUsername            : admin_2\nRole                : ADMIN\nFull Name           : admin two\nStation ID          : 3\nActive              : No', '2026-08-07 16:41:45'),
(130, 4, 'Super', 'UPDATE', 'users', 'admin_2', 'ID                  : 7\nUsername            : admin_2\nRole                : ADMIN\nFull Name           : admin two\nStation ID          : 3\nActive              : No', 'ID                  : 7\nUsername            : admin_2\nRole                : ADMIN\nFull Name           : admin two\nStation ID          : 3\nActive              : Yes', '2026-08-07 16:42:27'),
(131, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : ASSIGNED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', '2026-08-07 16:44:53'),
(132, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 16:44:58'),
(133, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : CLOSED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 16:46:40'),
(134, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : CLOSED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : CLOSED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : No', '2026-08-07 16:46:50'),
(135, NULL, NULL, 'UPDATE', 'investigations', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : OPEN\nActive              : No', '2026-08-07 16:46:50'),
(136, NULL, NULL, 'UPDATE', 'investigations', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : OPEN\nActive              : No', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nOfficer ID          : 5\nStatus              : CLOSED\nActive              : No', '2026-08-07 16:47:12'),
(137, 7, 'admin_2', 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 16:47:31'),
(138, NULL, NULL, 'UPDATE', 'fir', 'FIR-CA1-1', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : FILED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : 2\nActive              : Yes', 'ID                  : 1\nFIR Number          : FIR-CA1-1\nComplainant         : mohan\nStatus              : ASSIGNED\nCrime Category      : Fraud\nStation ID          : 1\nAssigned Officer    : 2\nActive              : Yes', '2026-08-07 16:53:40'),
(139, 7, 'admin_2', 'INSERT', 'users', 'officer_jerry', NULL, 'ID                  : 13\nUsername            : officer_jerry\nRole                : OFFICER\nFull Name           : jerry hill\nStation ID          : 3\nActive              : Yes', '2026-08-07 17:12:55');
INSERT INTO `audit_logs` (`id`, `user_id`, `username`, `action`, `table_name`, `record_id`, `old_value`, `new_value`, `timestamp`) VALUES
(140, 7, 'admin_2', 'INSERT', 'officers', NULL, NULL, 'ID                  : 7\nUser ID             : 13\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-07 17:12:59'),
(141, NULL, NULL, 'UPDATE', 'officers', '202600007', 'ID                  : 7\nUser ID             : 13\nBadge Number        : N/A\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', 'ID                  : 7\nUser ID             : 13\nBadge Number        : 202600007\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-07 17:12:59'),
(142, NULL, NULL, 'UPDATE', 'officers', '202600007', 'ID                  : 7\nUser ID             : 13\nBadge Number        : 202600007\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', 'ID                  : 7\nUser ID             : 13\nBadge Number        : 202600007\nRank                : INSPECTOR\nStation ID          : 3\nActive              : Yes', '2026-08-07 17:12:59'),
(143, NULL, NULL, 'UPDATE', 'users', 'officer_jerry', 'ID                  : 13\nUsername            : officer_jerry\nRole                : OFFICER\nFull Name           : jerry hill\nStation ID          : 3\nActive              : Yes', 'ID                  : 13\nUsername            : officer_jerry\nRole                : OFFICER\nFull Name           : jerry hill\nStation ID          : 3\nActive              : Yes', '2026-08-07 17:12:59'),
(144, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : ASSIGNED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : CLOSED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', '2026-08-07 17:13:58'),
(145, NULL, NULL, 'UPDATE', 'investigations', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nOfficer ID          : 4\nStatus              : OPEN\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nOfficer ID          : 4\nStatus              : CLOSED\nActive              : No', '2026-08-07 17:13:58'),
(146, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : CLOSED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : Yes', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nComplainant         : grisha\nStatus              : CLOSED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : 4\nActive              : No', '2026-08-07 17:14:07'),
(147, NULL, NULL, 'UPDATE', 'investigations', 'FIR-KPS-2', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nOfficer ID          : 4\nStatus              : CLOSED\nActive              : No', 'ID                  : 2\nFIR Number          : FIR-KPS-2\nOfficer ID          : 4\nStatus              : CLOSED\nActive              : No', '2026-08-07 17:14:07'),
(148, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-3', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : CLOSED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : No', 'ID                  : 3\nFIR Number          : FIR-KPS-3\nComplainant         : trial\nStatus              : CLOSED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : No', '2026-08-07 17:22:14'),
(149, 9, 'officer_smith', 'INSERT', 'investigations', 'FIR-KPS-4', NULL, 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-07 17:23:53'),
(150, 9, 'officer_smith', 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 17:24:20'),
(151, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : INVESTIGATINH\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 17:42:52'),
(152, NULL, NULL, 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : INVESTIGATINH\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : INVESTIGATING\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 17:42:58'),
(153, 9, 'officer_smith', 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : INVESTIGATING\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 17:45:29'),
(154, 9, 'officer_smith', 'UPDATE', 'investigations', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-07 17:45:29'),
(155, 9, 'officer_smith', 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-07 17:47:18'),
(156, 9, 'officer_smith', 'UPDATE', 'investigations', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : CLOSED\nActive              : No', '2026-08-07 17:47:18'),
(157, 7, 'admin_2', 'UPDATE', 'fir', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : FILED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nComplainant         : yuna\nStatus              : ASSIGNED\nCrime Category      : Theft\nStation ID          : 3\nAssigned Officer    : 5\nActive              : Yes', '2026-08-07 17:48:45'),
(158, 4, 'Super', 'UPDATE', 'staff', '202600001', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : No', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', '2026-08-07 18:28:13'),
(159, 4, 'Super', 'UPDATE', 'users', 'staff_arham', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : No', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', '2026-08-07 18:28:13'),
(160, 4, 'Super', 'UPDATE', 'staff', '202600001', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : No', '2026-08-07 18:28:25'),
(161, 4, 'Super', 'UPDATE', 'users', 'staff_arham', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : No', '2026-08-07 18:28:25'),
(162, 4, 'Super', 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : No', '2026-08-07 18:35:54'),
(163, 4, 'Super', 'UPDATE', 'users', 'officer_rahul', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : No', '2026-08-07 18:35:54'),
(164, 9, 'officer_smith', 'UPDATE', 'investigations', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : CLOSED\nActive              : No', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-07 18:53:55'),
(165, 1, 'admin', 'UPDATE', 'officers', '202600002', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : No', 'ID                  : 2\nUser ID             : 3\nBadge Number        : 202600002\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-07 21:19:54'),
(166, 1, 'admin', 'UPDATE', 'users', 'officer_rahul', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : No', 'ID                  : 3\nUsername            : officer_rahul\nRole                : OFFICER\nFull Name           : rahul amrwadi\nStation ID          : 1\nActive              : Yes', '2026-08-07 21:19:54'),
(167, 1, 'admin', 'UPDATE', 'staff', '202600001', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : No', 'ID                  : 1\nUser ID             : 6\nEmployee ID         : 202600001\nStation ID          : 1\nStation Code        : CA1\nActive              : Yes', '2026-08-07 21:59:10'),
(168, 1, 'admin', 'UPDATE', 'users', 'staff_arham', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : No', 'ID                  : 6\nUsername            : staff_arham\nRole                : STAFF\nFull Name           : arham shaikh\nStation ID          : 1\nActive              : Yes', '2026-08-07 21:59:10'),
(169, 4, 'Super', 'INSERT', 'users', 'Super2', NULL, 'ID                  : 14\nUsername            : Super2\nRole                : ADMIN\nFull Name           : super admin\nStation ID          : 2\nActive              : Yes', '2026-08-08 08:49:55'),
(170, 11, 'staff_bob', 'INSERT', 'fir', NULL, NULL, 'ID                  : 5\nFIR Number          : N/A\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-08 10:10:41'),
(171, 11, 'staff_bob', 'UPDATE', 'fir', 'FIR-KPS-5', 'ID                  : 5\nFIR Number          : N/A\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 5\nFIR Number          : FIR-KPS-5\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-08 10:10:41'),
(172, 11, 'staff_bob', 'UPDATE', 'fir', 'FIR-KPS-5', 'ID                  : 5\nFIR Number          : FIR-KPS-5\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 5\nFIR Number          : FIR-KPS-5\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-08 10:13:26'),
(173, 11, 'staff_bob', 'UPDATE', 'fir', 'FIR-KPS-5', 'ID                  : 5\nFIR Number          : FIR-KPS-5\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : Assault\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', 'ID                  : 5\nFIR Number          : FIR-KPS-5\nComplainant         : raj kapoor\nStatus              : FILED\nCrime Category      : hit and run\nStation ID          : 3\nAssigned Officer    : None\nActive              : Yes', '2026-08-08 10:20:14'),
(174, 9, 'officer_smith', 'UPDATE', 'investigations', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-08 11:08:02'),
(175, 9, 'officer_smith', 'UPDATE', 'criminals', '3', 'ID                  : 3\nFirst Name          : fd\nLast Name           : jhk\nDOB                 : 2008-02-02\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', 'ID                  : 3\nFirst Name          : vijay\nLast Name           : malia\nDOB                 : 2008-02-02\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-08 11:21:15'),
(176, 9, 'officer_smith', 'UPDATE', 'criminals', '2', 'ID                  : 2\nFirst Name          : jethalal\nLast Name           : gada\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', 'ID                  : 2\nFirst Name          : arham mustafis\nLast Name           : gada\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-08 11:21:52'),
(177, 9, 'officer_smith', 'UPDATE', 'criminals', '2', 'ID                  : 2\nFirst Name          : arham mustafis\nLast Name           : gada\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', 'ID                  : 2\nFirst Name          : arham\nLast Name           : mustafis\nDOB                 : 2008-02-05\nGender              : MALE\nWanted Status       : WANTED\nActive              : Yes', '2026-08-08 11:22:07'),
(178, 4, 'Super', 'INSERT', 'users', '213', NULL, 'ID                  : 15\nUsername            : 213\nRole                : ADMIN\nFull Name           : raj\nStation ID          : 3\nActive              : Yes', '2026-08-08 11:53:43'),
(179, 9, 'officer_smith', 'UPDATE', 'investigations', 'FIR-KPS-4', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', 'ID                  : 4\nFIR Number          : FIR-KPS-4\nOfficer ID          : 5\nStatus              : OPEN\nActive              : Yes', '2026-08-08 12:01:40'),
(180, 1, 'admin', 'INSERT', 'users', 'admin_3', NULL, 'ID                  : 16\nUsername            : admin_3\nRole                : ADMIN\nFull Name           : trial\nStation ID          : 1\nActive              : Yes', '2026-08-08 12:03:04'),
(181, 1, 'admin', 'UPDATE', 'officers', '202600001', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : No', '2026-08-08 12:04:36'),
(182, 1, 'admin', 'UPDATE', 'users', 'inspector_raj', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap mahol\nStation ID          : 1\nActive              : Yes', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap mahol\nStation ID          : 1\nActive              : No', '2026-08-08 12:04:36'),
(183, 1, 'admin', 'UPDATE', 'officers', '202600001', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : No', 'ID                  : 1\nUser ID             : 2\nBadge Number        : 202600001\nRank                : INSPECTOR\nStation ID          : 1\nActive              : Yes', '2026-08-08 12:06:56'),
(184, 1, 'admin', 'UPDATE', 'users', 'inspector_raj', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap mahol\nStation ID          : 1\nActive              : No', 'ID                  : 2\nUsername            : inspector_raj\nRole                : OFFICER\nFull Name           : raj pratap mahol\nStation ID          : 1\nActive              : Yes', '2026-08-08 12:06:56'),
(185, NULL, NULL, 'UPDATE', 'users', 'admin', 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', 'ID                  : 1\nUsername            : admin\nRole                : ADMIN\nFull Name           : system admin\nStation ID          : 1\nActive              : Yes', '2026-08-08 12:55:21');

-- --------------------------------------------------------

--
-- Table structure for table `crime_categories`
--

CREATE TABLE `crime_categories` (
  `id` int(11) NOT NULL,
  `category` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `crime_categories`
--

INSERT INTO `crime_categories` (`id`, `category`, `active`) VALUES
(1, 'Theft', 1),
(2, 'Assault', 1),
(3, 'Fraud', 1),
(4, 'Robbery', 1),
(5, 'Burglary', 1),
(6, 'Kidnapping', 1),
(7, 'Cyber Crime', 1),
(8, 'Murder', 1),
(9, 'Drug Trafficking', 1),
(10, 'Money Laundering', 1),
(11, 'hit and run', 1);

-- --------------------------------------------------------

--
-- Table structure for table `crime_criminal_mapping`
--

CREATE TABLE `crime_criminal_mapping` (
  `id` int(11) NOT NULL,
  `crime_number` varchar(50) DEFAULT NULL,
  `criminal_id` int(11) DEFAULT NULL,
  `role` varchar(30) DEFAULT 'PERPETRATOR'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `crime_criminal_mapping`
--

INSERT INTO `crime_criminal_mapping` (`id`, `crime_number`, `criminal_id`, `role`) VALUES
(1, 'CN-CA1-2026-1', 1, 'PERPETRATOR');

-- --------------------------------------------------------

--
-- Table structure for table `crime_records`
--

CREATE TABLE `crime_records` (
  `id` int(11) NOT NULL,
  `crime_number` varchar(50) DEFAULT NULL,
  `fir_number` varchar(50) DEFAULT NULL,
  `crime_name` varchar(100) DEFAULT NULL,
  `crime_description` text DEFAULT NULL,
  `crime_location` varchar(255) DEFAULT NULL,
  `incident_date` datetime DEFAULT NULL,
  `status` varchar(30) DEFAULT 'ACTIVE',
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `crime_records`
--

INSERT INTO `crime_records` (`id`, `crime_number`, `fir_number`, `crime_name`, `crime_description`, `crime_location`, `incident_date`, `status`, `is_active`) VALUES
(1, 'CN-CA1-2026-1', 'FIR-CA1-1', 'drunk and drive', 'drunk and drive at high population area', 'kheda', '2008-05-05 06:56:00', 'ACTIVE', 1);

--
-- Triggers `crime_records`
--
DELIMITER $$
CREATE TRIGGER `crime_records_after_insert` AFTER INSERT ON `crime_records` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'crime_records', NEW.crime_number,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Crime Number', 20, ' '), ': ', IFNULL(NEW.crime_number, 'N/A'), '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Crime Name', 20, ' '), ': ', IFNULL(NEW.crime_name, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(NEW.status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `crime_records_after_update` AFTER UPDATE ON `crime_records` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'crime_records', NEW.crime_number,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('Crime Number', 20, ' '), ': ', IFNULL(OLD.crime_number, 'N/A'), '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('Crime Name', 20, ' '), ': ', IFNULL(OLD.crime_name, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(OLD.status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Crime Number', 20, ' '), ': ', IFNULL(NEW.crime_number, 'N/A'), '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Crime Name', 20, ' '), ': ', IFNULL(NEW.crime_name, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(NEW.status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `criminals`
--

CREATE TABLE `criminals` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `wanted_status` varchar(20) DEFAULT 'NOT_WANTED',
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `criminals`
--

INSERT INTO `criminals` (`id`, `first_name`, `last_name`, `dob`, `gender`, `address`, `phone`, `wanted_status`, `is_active`) VALUES
(1, 'harshad', 'metha', '1940-05-05', 'MALE', 'u.p', '9874563214', 'WANTED', 1),
(2, 'arham', 'mustafis', '2008-02-05', 'MALE', 'munmbai', '9874563214', 'WANTED', 1),
(3, 'vijay', 'malia', '2008-02-02', 'MALE', 'kolkata', '9999999999', 'WANTED', 1);

--
-- Triggers `criminals`
--
DELIMITER $$
CREATE TRIGGER `criminals_after_insert` AFTER INSERT ON `criminals` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'criminals', NEW.id,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(NEW.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(NEW.last_name, 'N/A'), '\n',
            RPAD('DOB', 20, ' '), ': ', IFNULL(NEW.dob, 'N/A'), '\n',
            RPAD('Gender', 20, ' '), ': ', IFNULL(NEW.gender, 'N/A'), '\n',
            RPAD('Wanted Status', 20, ' '), ': ', IFNULL(NEW.wanted_status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `criminals_after_update` AFTER UPDATE ON `criminals` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'criminals', NEW.id,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(OLD.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(OLD.last_name, 'N/A'), '\n',
            RPAD('DOB', 20, ' '), ': ', IFNULL(OLD.dob, 'N/A'), '\n',
            RPAD('Gender', 20, ' '), ': ', IFNULL(OLD.gender, 'N/A'), '\n',
            RPAD('Wanted Status', 20, ' '), ': ', IFNULL(OLD.wanted_status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(NEW.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(NEW.last_name, 'N/A'), '\n',
            RPAD('DOB', 20, ' '), ': ', IFNULL(NEW.dob, 'N/A'), '\n',
            RPAD('Gender', 20, ' '), ': ', IFNULL(NEW.gender, 'N/A'), '\n',
            RPAD('Wanted Status', 20, ' '), ': ', IFNULL(NEW.wanted_status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `evidence`
--

CREATE TABLE `evidence` (
  `id` int(11) NOT NULL,
  `evidence_number` varchar(50) DEFAULT NULL,
  `fir_number` varchar(50) DEFAULT NULL,
  `crime_number` varchar(50) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `custodian` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `image_data` longblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `evidence`
--

INSERT INTO `evidence` (`id`, `evidence_number`, `fir_number`, `crime_number`, `type`, `description`, `custodian`, `is_active`, `image_data`) VALUES
(2, 'EV-FIR-CA1-1-2', 'FIR-CA1-1', NULL, 'IMAGE', '2 people injured', '', 1, NULL);
INSERT INTO `evidence` (`id`, `evidence_number`, `fir_number`, `crime_number`, `type`, `description`, `custodian`, `is_active`, `image_data`) VALUES
(5, 'EV-FIR-KPS-2-5', 'FIR-KPS-2', NULL, 'IMAGE', 'hit and run', '', 1, 0x0000001c6674797061766966000000006d696631617669666d696166000000d66d657461000000000000002168646c72000000000000000070696374000000000000000000000000000000000e7069746d00000000000100000022696c6f63000000004440000100010000000000fa000100000000000087f40000002369696e6600000000000100000015696e6665020000000001000061763031000000005669707270000000386970636f0000000c6176314381010c0000000014697370650000000000000272000001a2000000107069786900000000030808080000001669706d610000000000000001000103810203000087fc6d64617412000a0a1866271d0b0404341a1032e28f0211400104104140dd2b52eee94e53f4ba9215d456771d1e5fa1b98da0677cd4748b8418c66bed741e147a5dd96aac2c75a97ad6e30ac2efab3ce8e2f994d6927f9ad4f68e17fe4adf15e0e5718be12cad9200c867378f7fd2eaf1079e37e8bb7b0c2ac7cb3eb460255c2da8998142693e66e60de61433ef41db23cc2a4c529c45f687e32b35889da5ab365f012244daaa3e3cd979eb7fb6fee3f3348065f31d7076fb83173ff9f9889fe28e9bda2a1ba9e605ecd8e32376b0059bedea58a00ace9244232000292039164d7c629261401755bba9ca335ff347610ec4fbbcd570c3053e36203554eee4ac263157ac90a9ded7450e0fa79c7436ef9923c9923ab84eba83dc3d18203e126a281196307c9c99e76b1f50cecd04fa21f7efab6cc815e65e0096c841f6be73bf5103c916e02247bf1cf817bcb93f692ac65941af31317a51bf7ea8e6ac4c926d00e7801e1de40ef86b2dc895c01c1331147b0a24e83eab4d07475c43b5691efa461a08dc56182f16ebfea7fe05acfde42d3100dbeb2dc615a83dd7ca6124f9b7f5c66aa896f9fdb83b912da0d9bf99f25a722705619f272f39e19b07eb99bf905de4b608653222c45f172f7e6682eb3e12a526df0610735ea77edf110097c1136fdddce06c203198227380bc0c92ebfaa35750794527dbce0bf165fa7402ba4b39c5809dba8b823fc867842ae065da279f362c7cd86777cb4aa4e6144d07a527e07bc6754a87ea9a500e5dae38a72a68b05294cb71be28c804067f11c615f4a01b3c0c7cb2cee3aad72fddd9908761bb30b423bc27c834395307c50b7efcab858642b6c5b8fe5eaa754b6616f843e054b8f7b07df745e98b9ab9c0b3e53d80e959408c1cb1b9c3bcc54fb50294c7d9cfb9cc7261ec33e98c17a0812d1ca58c1a870be30d39c382d47d05375f9e7004253c9c2eac245acc4933dc44b0521354f268a670d871c32ed08636a9e9ba09df861cdbdd590b1931b5e52ee5b589e5d05354246cf8ae19ec001c517481b9e7a2b53fe5600c249c224a558ef87c708896867db4dc74f0e63d61460677c3331202d188b01b1a1a28d3d613fd4050bb592186fe9560854e635d3ceb31597d7086c4609460e9d0242895064ed479fdcdb3935b75daa3a9776303874bf0a353b84e0995080131e50d0433f09f37affa8e270e14267c238c028fe148f07bbeb472bf3135c87c25906a65e24101a2332ad491a9f1620f2175ad59597e4dc54022221cc71b5ebbe852eae0e2b66c54aa31526cd33b9c74c66af98871d89cd736cf86177fc7ec69f215cc9b6f8a5c190950d2c0ca2aee63097e262ba8a0246ae568aa6c1dcc282ab8f2f307c1a4b6024c6b45651b5e4ac899ab0678a61af15d08d1044099497096651d20dbe6677a87ef3e068d415b4c6e268e65f04bcda63521c848aa756cda6f91ae6c0da05385a584d24b0b41f9cf6d607eb06e6b64de50f040547d0124d9f5ebb56ab6f72e6759bf1694cddb399da6aa73d53cdc48adae4678f720b6da50419867c23c8ee1f461767e29c101f5cb2ab332e75da4a75a42fc43cc214a9aa1568e151412620c526c1556e7581a19f35f4a989a6fc6bc8bfaa0c5fa217b55f12f6a449e9b44865634a4d0ce8ce889f0ab8389a998aef970927d967c41c7da9746ab572be628497848410e5d635778f5082dcd9b59ea385c9a8dd91ee6e365256a6ab2804975e8626315ca89f9c1599bdc7535b762d649c67e39ecde2992cf5e3b668280e6be762c2726b679efb824849c43efdf3e143a27e0fc0f2baa2f804379753e452af9d91818eb0ac6f5c0abeb47d1f2abf75ed4e479b5b162a6c350600b9126bde64653b08bf95197aae5934938ac4852c4171db60aaec270ff42c2bcebb17ba82fd8b7d8f79b4ca74450d9ea221c1c94905d4586978347585ad53cf4c12f1370b1fa349714589ca37c5f1d6789eb6d0b0c24ff256f3337ade2cb7edd474adba4c674b6d04fb9563fc40d5b833ffcf2434ea5b74d4df1217db67262b1951ec70fb6bd13a80e3cf4b265f990d691b645bf2bb20c56f18d6c573e3773d5f3bc4ef4e0cb8759536badf62d2b1ccf598809ba05cbd6b7e284a068ece7f0deaaa170fed1a2ab831040b29512389bf21cdf8a52474482e4c278e310a68d6f4e3f8e4696c9475e38b39e108d623f63a2b9217c6ae64534ca2badceec3efc0b58c12a40f98e439adc67b924dcc0d924d97fba59523240ce4f4c7a64b94708542afb903727c7f0b9243ea928805d2844518ace48ff01daaa204124c28c8a035f247c86db598755dbb01b5f7f3beee33b1b05b7d5f32c05fac4ac710c7829f62230c62dfb5171bc8762124d32d67ded5d9ae9e7f1c7320da08a1c2a1558663c98c6e09b1ac4aac46dc345498976a52acb3ac83ab98dcb29a5fb3a2ba9afefbcd176100c5980bed2631653b8d49bd7bfdb03fed860421f3d1079b4628c808137347074c07fc47845d9b8752d4f809163be0d54f792f5cd1c11c3ddff6778660eefe1ec1f8dcf84b782372a23f2796ba7a6ccf4539c7114212be956f73d460820f77519f00e793ed6cbf8a04fc871767a345178d08366809ced7a43ac98c97c7346f7bbdf12fa53d2bb7151a5ffc1583d7213bc4567d8d387cd84a17a555faecad6d58d679e01080f5fd6e5425d542a0f3e45a12ab7a3b61084fd792596f72ff0f262608771052c6d2fb452d0016cab23ed1581f2e07d9b741d7931512135ca26bf4c2db71d5a918ba722449d5031a38388baf027504adcfca20f68497cb86f75eff57991785156d0bc86ef272167260c3e7441f367601349f495b78f7b5c6cd975377b60f7a97336d9731292a0726495b632e527da5a56059b68d706438a9c2ad7a800bc88cfa2c55681fe154a60bcbc7875ebfab557ffc673640356ee36dc79c80829054e1c2b5e40ae93131c76d15dd3aebe59c7bbc85e6e3a3059cca0019ed4575dfec135989bd2bbc0fa2bb0f98179b75ee6a241c70fa06176108471e900644e5e2c68ee5dde53b6ff4dc1448ab71f1986f2f758951a5abdef99c7cb2b64c616d77f46ae743a98672d9b8b372a87750d616d994c0845d124c5183bc01b12a3e845e2545ee893bfd86e810234a85e67f8d47c8151bf30b7a0c4fef3640510c8c7839cabf054700c1b0a3ab3e3ae805821d788f996e65cd250967f1d371008e26198d7dd438d5b796aa41cd9cd119bb33767a1aa75e4512be81cfcb77e69573ec7ca1ecca5e6830d93f65507696ac987ef9788378bffa865970d934a9d4afe761d05eed953d62820cf61ad722db7c804566bcf6611caded64f0d2cf82ff7c7761253494b771ef40448da4abf5a6dafc651967f431a6dd88f36ce673b3509d8340805f36424872739ec20ea7321be538da2967734a8f9508af718a91b5a4c93e507dfb1503f228a959cc17cc44bbefba46bf045f42b1c2e99de431dfef9c8247532b1fa8d15f444c3e5f7be89dc2aa849e600259b9bfcd703541e0a87ed5510413e2b9bc91bc247cc9f4df3c60ce1f2d95d398916f5383df028d81f2f3d9b4b011464bdaec157d99023afe7cdd7290bae4051f46dff5f75fd1ece3eccbcc1d73d87abd06b7f3e1091413013ec0863c1f88369d6384bd6126ea8bf28d46146af9b83e36088edd963e5d7508f272e85303c7b45f28305fa0694ea0f88afbceb2202c4cbc8268e16e51595555439bf5b9b157985c4829ec5749b6c4026fd402e98189868d9de338995671ecfd9c02a48218ebd39a487b787c056e81ae37a25b19f99b80d6086c0ac81052c92f5742194d896834431c1b00bd63a1c910fb57f4056b4183e3f860d2dcf951d730d5539f5445fb4c09e8da3d4f229c46ba478af1e1e5b3e041270e946c6a09d62d657a2209c949cecf85f47dda5e467501801a1e3ba51fbc9eb5c6b7fee0c90d6d4aaca80ab27c964fc774b0a907621de207268b458df23c185b228acef53673517b912b7ff9f4eca00a0ce4cd97bf6d496801610c9c0d7051b3f4dea44d3527465f72d0212cc65fc67838ffe7e1bba64b89055d64d6ffc3f512542b85e04d72750f6efe06edb4fb1fda2fcc81855d22b002a3ad99106a03d6e7052325c7c7e07f6cd29d0295ca1ce80a03a7117d328552c50f87eec6a2366d6c3d267c6e30295a68f29abb291a7f99e17a13cba7f9686f41beefc1b24ddd7827b6cb31b519618439f2ddbd24d9e139033ac45e323f7401cf478c6d37b7ee8c104fd38a8db3bf6f5a413bca5046922fe19047604911404b5eadc493ea10525ce47767bc48be90b96f3df25b71e96419da2425633a6a20b1f32f3babc40b9514d96797f0547c6b420ed5f1244fcea19270a4d71be0b0578e607a062f269c3372c2c589c06665b5e2a028adec2bd9a4304847977fbc103f1aeec7548ae78821827d29d479f7aa889dcd8f6fc6607c8728edf655e0821ab27f7c8f4915601624798cd9e6fb0c411189292f53e204dc1ebbc9e005647543349d66dc38d6797e352806adc7060ae5d204cc66e24a4a2ad5d94e255f3e1a2dfdd3634acae06f34a902e72e2e80178d2ef380a82660cef87da381361dd932b582e7b0100b55db29af6afaaf33ac126301e123f421ef9ea6f3ca7193b276d864ee966fd6a799290b5d7406143ea8255613b1d93248e98d4ace08c677b87771601ace0978e616bc4fc6d369b554ed42ebac890f6e7f05061abb5ab32d5973eb5380f279acfe73d30dd080ae903cc9bc07299cd70fd51f904633fbfe622244361965f70092702c1cc2a92ae3b2c3f5227e5a9e89ac10ac1d323d2b55c1d75c45e85c2b688f61df1854139abf80161eac02e851fe2774d9d64b7fcc948ecf9a7b17c2420ad26903023e5bbd4b49c518cb80388b190f876b8032246530f75e61151b768205d78a2e5f81c8e208e332aa7fe675a7e2388b10103eb3ea559619ad9ed318013ca4c0a7dbd00911a0c75d6eabd9be7ad0120eaeeae42f5931dc03d6bf4642893d118002329c4e7f7fe836ecc14c60247bc2e577e3dd448f4ac926a2e22c694cf63f7881aa1b7abd8b743852257b609a473f675d2cdda9b1818af953f161675f9427fefd64a6eedbae39edde0ecdc970a7a8beb0219a64b666a01b1a4b689b435c7c21669d2e90df6514d8d473fc510289d2cfaa57854a32e19ff0f8f81f4056842d9b9588d7c246ff9082a32b81dc30d4936c85b2f12cfc78b9a97e2da04b6b1a8a8378d9cd272e69f3a6d6f363036985ed73b68a34822254624af4011e744ece9dab24ee6a9af26e1aa2d71e31ae6cecaed6151f965577a9c21c864ed55a8b4e6f8cee4e6975d59fefb74e379674a0d3f4077720b6e49463a653a601d765e6ef209cbb3fd587d74d3f2685f9060ddf7d479c5a2a019aa8023d1a19770117ebacf82e934933ffe691b306ca83f50ca24bdd1d00f1fae04acbcfa27547c0a2b8c2757bce35126085a0877e264a75cf377253f3e3381b3cd5d5c6ccd7e6c07a7008da586091c5514aa7d97c479ba61bdd0cd2aee3beca9c6dc215eaf5c86379aa00d0dca4a180a1f702e687a28d1463e2ad33c04c5346dba5360bb9c889250b122592ea3dd78e63bc4d898dd761ab0fec0c96a9d4108f3b1dee4ecff0f75b79e12d5884ec30a30f478149815b19ea96390fd08986b50045eaa1ea032e7b5bd8357953762209ce0efc69b2c37b44b6aa62e9ccd743f926504e9aa893406c09d250fcdd1f079427a95b00b0b8bb7a0df14ecdf1f0af10ec5b15212e95ac8be3a2381fc928abe3be8e8ddc8fdcb4f1fa2de1726c91d802d2002d25a2dcebef714bd4615fd35ccf39952196781867c45d6e0de5d5ea866e622607e68cb759d2d96ce3b1da05a67d854cbcb845a99e38a944e66791a21a401ea8a27d51dfaef8efc53128c339d8d52777394b5aeb8087c78ce2fecf16de2f27e5f3c7db80f27ab03d639e79cd385065b68841938fb355fb0b71e02d1c501337fdd821d17e6ad9342071bc5a0e45f3ac0379f32f99f3febfc0531e18512a63c28d550469180d54975ba5e342112d456c1db91c8e809d631c8dea15ca5cef491d3790559bae6b35f8f5f678f28674c64ecf68ae62097d3dd91dc539a558f7fb4bfd62128d7e47c25200b8710218e32850b2fbbc69e3856e89e6c1a219a91b7fd22cfd803f4002b58181ccc00533a01db4d411985eaddb364f4702d22ff78240baa53b2242ef1c031c536197ad099ffcc7744990d2fd10728d3e9639806ba1469716d8ccb9b5062b9e932f01b2abcf69fdd67c86e560b1cdf0b14cce25eb8c5e395737f9102a55e5e7885f6a794ed8594eb162cdf470ef98c226bfa723ee5c97b63c46c56868c7f06eec22d68af90c0559c550e8070185afe6eecdc4c8127332e8a93335d45b2860f965bc74ee68b7627bdbd41b8fdbc64ea79a1f2dacf5f79ccc49c7ab6d0d82f813a0e49c6879d89d8f618c5423fbc7e0548b2adda2e88b4970d2c4d27d7239650e2a9992f92a97dafcd60a23fb413e7a5207599fcfc082c74032470dbd61c71aac8773aaecd2675d8a6eda77bb4129a29b2a28b5a67b597f3bfd47c93ca79d8b6aae8648fe7db610b731c8b8ff855f9ccd03c0b91abfbb9347c33ba47619d964b87e5e4baddfb2646e2952d41de0c4f61d505578f0475fc907037a7e48df056ec73ec5d903fef6a1535cea9e40120c3c50f3ced22d2d916cfae25594cba747ba623a7e46cc3003deda2f1547f0b62d36c59f62e2d0f13b3336ccc836d360c75eaf14232320795116a9a56c931f0e8cf37eaaf8087e56a923fe219aeb9ea89f0871933518ae78da1ed806042ad1e3f6f64c53a48d56fbc501bc11f268bc1d74dbaf08a8e9b230f836a48960c15b871254853bb8438d1c6a63febd824030927f6b4cfeb7c586655173ae54cdf1493b18aea26e7f9be6aaa11e106d41f5b2a9e3a10477d21c4df9b607c45a1e9f97d9b85583d691dc7f3822870e2aa82f9ce773306ddbad450d78cc5f8602964318990cc2a6777bc7f3f578a2b44aeb704b5126d27fa438782448e5cd26888f8a95caa5120c8e458cad24d1300ccf34772a6e48bd06feef6fed1e20544123c0625d8c03882e7530337d4f6f4ac11641b86aac7ae07f8ffa12eea13c22e895264612ae9b08feb7596d730b0c40ae1b32210a0d508c95a790a5a89d451d3b928845e57662bc6c7e426c8a19d95ec0e76f3bcbc500a769a962eefb703cafdaa48e492de75eb1ca94a14e250747415bfd63db67f74687d0855f8bd0c92932f205432155caaf43e75b0868dca7c8457dbe874b3098881082c966b3f8a3e36549dc38f013a4a0d53ace5d386f44e1b840c9bd8e89a800f9669228ff2b19bd15bdb1b6a5f7ad87ef5722096a46b003f01456af13090fe70bda6989e601b34fd034b10770b2de33b7c091f21dfdd365e0ca14c0afd82ae1c9cc3a112c43d631f12b71e23af536d97b2b2786c8dd3a3eea924b32d3f753a05ff36fd847b565c1dac585fa39fa8865966395838aca380214b098ee94ddb8cace497389c61bed3ce0a0f41d5e14e88f975c40a5dbeb9982daea165e3ae06a0d494cbe450d6e959a4359759921f07f385f4a6415528364589b483a14df9b7a1fe0f5a66c82ad4e063bd7fc35dced2e0c3ae333005ebe4cd2a03c3c13f9865a6b140c0916f31189bfcb5d17a6ab4af39987e60e28cc5409b661ae462f2da4d60751acca5bd9e86f8faf8e03d117d5f4d75a2a0e377b9e47829654f1d5b0df44b84555fcd62ac4838c13d8cd5a4c113a432eabcdf19ebbbbec4ae2e8056f411957e7fb5a7c0a0ecc905f42f7b495f9dd1a4848be8162496ea33f4496cc5e1baff1b15c951ccd589b48de2cf361a661d76b3f3702c2dd4bbfd5297846a4463e8890c696e85195dab7476854ec50c9da3ae473c5b3f6bcbaed37ed6ebe4498183439f6e9fc874f8dd92b2374854c70c6432ce656b7ea3cc5201c21d4adc89c4647de9a346293c6aee2a3085a7a0513ec24a0268d130e1dc5b98709696cf308339e7d0af90456b78f5f13c7caf82983fdf487e4bda797fa24edfa5234c60c987374bff1bfda0ec1578bcb45260fa57835f528e946348c01efce8ec7ca91a47d0b1097e20e32d63f83f6468fe6a175504537d6aeefc850f8709012d05462f126a47ff39be8cbe2a9c2175d1b1bed3006c0f9dded588fdbab14f5a576d7d05debb45005dc9494c2f9955781731b31c1daecb6f6d405187ca783eeac0c198d4215b3884471ce11f67693590b69ff2a89d546279249b9cc0217aae953ec5494df4155010b881e50f17a2db2dbe083ca996ed5c66de99fb87ddd935775ea5060577cb924c1262d52a843a2bc7f5789b0441ec1d86dbfba1e367e3046fa3812f32eeca9d8dc326fe5ef22079ce72f3a4d7dc25ffa4ccbc0b017f97cd816ce82de1d44b9acd8934a860dbf5a003e89c78d08f8c090068c88d611e4106235bee7c9d8599b8ed51e11111afc7865299822530790164c97c6d260329c56e0f30351d13d475494699f4789a0742f6f22e7f591fbb630ca08fd9d16abf13a50623b8b279bcd35492293e48f4a19fd1ffab661ffb17f933f9f42ff26508aeb5021606e27a537d79a19358b244ca68126767359779c63b4d35511500387722d49208301e607fa8a73683ab14e55de5cacad7b3e5479a8e4a9f1e027caf1a11067da69fcec2ce4316dfdacf69ab52906fbfc4a5a4a0ef0da50f0d69b4255698a0162942676b4dc1d5d8c7a02f5b586d7bd1017111ae7f34becbb3f3b63560979f82089b083aca422854ff3da347c180d6d9ca64b2e40303dcaa91f3d8f0b68aa53cf8912eafd31e660ec1d2597b991e63cf2f34dd5060e60571c9f27038bd53c693117dcbe9692f41560f30bfdea680d409a8d63b27cc627472bc72ae1ab565aa899027858489fe7545c116bb374085a92093fb083a2aca69aaba2d7de4a253d2989c09150a809b333cc1b3365602d8372903c9904cfe2d4274c61f77bbb87647eebfd80cae8eac01dead373339bb36eb426fa7bbce4ec18e52ae580a2cee45e4be974afbc24bfd74fe06457eae31fc4ef951ca472df5382d9049e3f3837b7565989f3d28ef88f216d03dfe6a8b126e0924e19d3b742871beae6284ccee7626f31f8fb814e608c7cc1746ef8d413b66c83e3a041226327e63cb8399e55d0d58f134b1bdc9e7a92cfcd9d166bedbf653637e478196b8a302c2890012e0768508f25f427e7317dcc19502c35b5611dd66cca8fe874bbb9d7c23138b4fd8d5c51f0a0639adddb3d0aad391104c2e7dda15b6aad24a483453937168c120fe4d9b3c61ad56fe6997b14b9c486bb4c2e39ab9feaed8ef7f61fd98673ec92044f357d32206a95c6607872df38ac7408f0c453228489fab526cd72071e1461fce81bdd704da1927187152fe5b42ef4369e738524bcfc8e8c8175816466bbfdd12205fc2df5a9c3b8620c7bfa99fe4d584fd64dea62c3e37320715385e33504267b9617a2e4dc8731d6a295230a248a84ec3f0d1538ae61a274e834c350b007588c9ce0630ba3fbc21caf45a111cd42304f9c42d6250d639cc8bfff1516d85656dc265e23af11477c7c0d10b8677158f8909eeeae4436d277ad665759b7544e12b9bd5e1f8edecfc4485a04d051380aa2c4a0f144a5563249dac419b105dae37c1ca914721328ccbfe9ac3b2357719016ae449b58cec5183b61282f7c98753824404b1d9ee0b286a9d09fd7768081c4bc586336cfeee372542c9a4df48b569d2b6850589d5e3accf6d9f64332b2e6ee7c0d498c092e3d268a65c4629ee131068a61c1abfa56c58a99e9a49b836bc66a0478cb11accd3dc99bfdacf145a72cd4bb7421c7932d300d2d02da4e26d3a5c7b283060082985bdbba068b476de2bc7df1dbcfc73232387c2b69079049b5fc6e8116e9aef7b9ea297f2a0d4520fa7d2546473fd6c3e809268978be4bc213af59e2dbd27a52c8a8229681e35f13be19c79916a6c060ec1ec88db55429d34e68cecc455a479449814ac3d432f103257532f078df2658dc7d3c5551ff763ce1f1866f20e9a4dee82d5b0c51b4bb9f322134d02af6f38a81ba1062e3786b7a1123988167710571f8a58c67bcb6f5cede2c1b3194c123e50721bd92aeaa3b30b868186f48996b5f9a370f0924970ef08052c95641c224a4567426fb75c89457ffa1f8a6461573dea49cb643b8b12479c18efce80feda425addb4cf305d8a9799e043c57a342d92f1b1710b474c8407565dc480935df380bd7dee7ce3cc478478c697a82268a809030536ef708497f4c8a78e5e4fd60069abc37af779ee46bbc9964fcb746c310d7f886069ed041a526d9ad379d26796baa7801c7300aa5c92977ffa04cec946eec26096c1b96bb73de65b767948d3546ac16af2dbc881d034422dcc68650100bffcd90f2c70e68f90152f54deeed41b7bdcffef9cb3412143cc02d963ce6b23715de0988c1dcc75f63aa22f20868aa426692fcc7cf019f2ad866a5cedcdcf254a5f94948df12a47f8fad8170c3df62005867244205846c9e260214741bf8704ecf4987cf437f6faf877b8931b58a3c988af5bb28c6535102e469c991d34eb5f9beb29e89d8f5d8b2e444cce5918a120c628374381ebbbfd7638e3f2d247c567851eecfc9b4aa19c3d55f8446010f4eeb3bc2b3a502d98fd4d13a23e6ae4951a7f467fafd691b66c755c053617e5475fdc3bfcdf5fbef2f117f6e608029275c09f2fc0cd10bed52f988ffa9074142e7b9a8361a430a9ff3a3b7704a65f1ac52323e5e6927719a96bc9936c3f8b33a61b857bb4210127062c7ce8cd283e2b133339b1fa11903eb52df16083f1fad2a961388a8c9029d8f1faaf3137aff7dff0e5203e176350d122ee2d5ea7fe7bb1a17f4c9c800e79d5281f3a9fd4508fa1201d30420e61a7c13b236d80c8e663eb794bcc6e9ede4d6e5a4e264b6ae8961a1a3a5fd56af04bb5546095f0e8f903e77697867e7721f0999de9648e3f6a9ea07006440e1e463dc10db905f1c22815264b5d07831136e42a8ad2a49f40675811a321671aa0218bc161d220ece4611563b2d48c1a1125d02adae5bf7426e84c2e4369af02abee6df0cd68760823334604a3c8477414b447771a1227c9087fc82f5aedad623737a31092791006af41f99ab3f80a8c219a5892b50b26b54736f853d3101dbcf58127f176a914cd1f3e17b7cd1e47799bf47f18dd289ba36818c66520874603402e56e16bfe98e02ea9e71236d5aec06374b99fc056dade4d3d8ca0c6593140711d1d8329d6d359060c23868de9e5b4a4256266e9e4652b39dfb54e91035ec92f44a849b1a5d5f8d16c9b035333059727b2e32f9b9fb4fda22ad2f3d264d1917ae2361c257d2ee7dcb7d00b9b18eca46b14759c96c800b0040b23a2f0bc2aa27cf68f35874a1a464ec8b1ffb6db09e7ab61ff3c4dd8490d7f43569e66e6f223bc6d6473487abd6f76c910872910980e4f977e55b56fc640781a4a217e6fe9beb078a65e178b6b5898386b0c485023f37161abec850316905a5330d3e30b454b0ee26d69f3ce58e0901e364f5e586b03aab9e93c78fa8e35bf612ce455981ebc1e8d4c5a16a5477705bd155de5beac5cf0b62284a618af64205f65bc1db69fc9b42c3969755a45662d8cf7a69405e54170b2d9a97bb8da14dfc9e7feb2788ed50a1f4ea4567ea09f0cd1931434718dc365f211de88f8f1333d5b015586a95167d6eac7d29e1a7c2b1914bd13ce9c94325a7a491876e767e92ae11e97a88001345fef350435e0b24a3032665ce522bb74c2e2936cdd309c0d60e5f343528f8632328d7e7f6366cd17f2b9a13e34fb4ee411a41e0c6aa5159cfc14cc27fa18394d3be9d62ac01d1aaecb9547f185e6a13f6ab648f47903ce1cd02c2fb6ed73da60fd0a42aac9294a9251837ae60d9059036a1b29335058b91f3f69e6790db6e090fbd80fc8e1e3e5b07e7109603a79db5449e3d458ed06caadde8bcb5ab360f837ce07d9f85d6dde64a1606e5384badf8f4d2f41d6444422ff0481b449411acf9d45ee1eea1429cec23be1017d38af8b8b1b2d88a427705e9b202872582a1ae54553a63c26f037cab48007318f0c1d034efb97bb785ed85c43056c7f86b35aad5fbf4a01b5a74dc14fa4a6299bae8fed68cb44439ee93f6e1bf2a41d6c06a269a14b03a70cd2c0a97dde4a32403b4c93f003e265c4dab0c769d0328054ae8c0fe9abe6b9156c3d1ae46b1f7e3c9d492a39dc28ab791816bd21521117455cfa65460e2ef76b6bd8f6454223ebe8edf84dc225f7b22d7db0513925b0584b05594ac6e1f890b488222e4ea199028f39f55de9ed05fe20d3ed99a1353d3b330ac1186c4ec3ce182be4388923ca262de8678e6855d51f5ca39b318f796276ae4ccae623b2766c6ec3c16a27b780931b9a9c026b75f1825bbdae89d7ff4dcf40661fc4b3d0046e225c269dff107c2cb825bcae40b33f95c8ba2be122e69d4f04fc06d009bbc0ad097de91b1e2c00261deb40811e40906b9d5420b85985042f76fdce88f30ecd0a70cacd00c4b97d2ee35b59826ac87d357bd80da976f57495f2ec892d6e42ea487da328004896e81d9c5ad60bab3596d8091ff96106eab40558d43a4c1f68d23e6aed5bb7fbcc5b45f5fa5c5e1e8123636986bf0a9889c3bb7238c09d0a23220ceee44306894f7f001fee6e252c50d3152bb24e7a50dce3abf3e76f60eeb5f6b6b78a1ead135149311e92f6bd200c5e1a9c551aadcc65537790bb145bcd7beb8f963250c86169fb4008b540a375c6cd5c3d6266e316ef4840b02b4d877021ed33875461ee1bef0c8a40ac759c54b711c02e848fff1f29350055e41abc79a8cf77554e82c6752b7f377bf1e96e0fff16bef1aefb90bdb2d975094c611837a9714828d139236a20c6cfc29ca9be664b375def7de4efc65317962b7d636d95adbb96111db32b0752f80600d2f5a92aaafe81c7b1b451b78386d5034829cb80ff940a3261bcfd3d1b2e737c3d1f92a6005449ddf1231ee899b541ad41926cf20005a1797c6e19f792ab7419497f34ca7a8c07bd89becdc09a4d8032782aea1d346549aed9a1ab72023a5e280254408b18b62b65a16428b52371d203c9b516f9d150eda1dfd391d76171449b44efcde2568429500aef4977723926492c511ae013707755a827a05e776f2324869f06e0a5bbb69163e22016416214996b4b88c3ac4d213efdedd6b45df36246efe39fed65c963e2e782fbfe720879243a0e3cc9edb2fff90ebc28ced9554cf9355e81c3f090842760ef9831f7a1007dde89a5d55d159c009a1910998892f8463abe659d2be02927f00787871a46fe1d364ac3713c7a28575c7b1213b15b8c5572203e36aa1a73bd262bebca2f407489e5d0077418476196b971fd178be622572c147bafc61df214559d3a444a2c4fa8c86c378745d4928e56c723c67b792b196b53c8aa7b42c5115feca424995f021493266211b9e3fa1d000e10220c8abe52314863a3d7bac36b427285dd623d996772fc107cbd4bc3d096e6b59051991b63e3a374c8f1a88da7fbe9f48fd40ff294c806766c7755327ecf262e77231510cfb68257a586d5652e02760b41f0649d00288e2aa917cf3bf10fee213f5ff1731160ad6d596b049ae409ed7a373a8c88df5bb6fabea4cbf8f998288ff7b1f834f90dabb4a47231b79644997dff8533bd695dc310c7ee86ca9ace4416fe90833140fc9204b3264e45695de1e63934438bfca1f493b4e7dee08ad79a1a672d5aff8f590b62e6359a53e32b4b8914538588615421c8c7f9ddf714afb4a1b7bea425dbf3693d233a4b9c4e2827e892f7738125bceb87cb7a2a82c1b06c5feca07dbb51ead531e48ac22e5f53d6327235ba238033c1527fcb0ce2a1eccc0885e93399668e794d6f86067b57a3a53e30cd93efa215b89ba7eddb2569f8ae853b4c6aa7c74aee7e5f5db15adf2b5c67eac3d1b52ce62bfcc7fbd415c7c9dc98560158c45c70866676e83772b1986797527b98bdcd68fb270e98042547d2eab9d13a83b249ad5356afcc811755c90bae180f44eae3d774ab9a6638bf30fdc2db2e33c98d23dc31a9b7956f6b87becef4fae780934ac646ecab979d89e1307ef24e8a5015c42f63583cd6d7496f8072dcedb1d350036b453598b20e83fa0773a5e443ddc3a8d488ef1be9661636c58dfa14593155c05f5ad47d1fbfa1436e045c866273598f19794b70fc4a33a8a43add94ab24916ff4a19a1f70a5170a32152e96a0074e12392a2916cfb18a44d4faff5dc70381f04e984e3eb71140fc7d06c8b8d716495464529cd432c4840d288e53c75530a0322a1ff193a59ec5ef9eff10cc336ea79fa9d6557c4cbb38f08682a5683ef6564a3d730daea738246d7d47c09ee7c0b53b0eb33d7fc41d6288bc282c4a78a18a477e007f2cd06bc47bd8562df1c38bb139e965371e700a105e9c2b617de2683d85898df2121ffb0ed8cda5f68b7671df930a0207ae6a295c114784f4afe6ffb6be5ea05d31ecc88145b21f11f5fbcaa0a8f63bc224168ab653eece64fe160fef84d05793e1260718db3a8143588b9e855f3f20790b17b3bd0d5eff76ff6a5f4eb5432556d32fb1b7e0bee30ca4fe571fa40e44a3c6c361b11726655ab1e9a83e029760aa533c47214a5847b163efdf846f72f13af93a4dda1236a0dd26244fd70bc7d73a1eb8f0786c16807ca87dcba2ee0f7f3757ad90d525ed2683f4059271e26bc68fe21cb586c2e3ed21d38f0a7ea64d74d4faee390fd8a0f664a296b6a87fdd8ec1e4c9cd5f2ce0ae56a68da7b587ebb645f580ce578ba0bc60e2842453642836e91db3c11f2057594f97745f45ee7c2d01b0433e250b0f432bf4ccabb882e64f001e1d45c562f418a7a99e11843dd852933e641bd0405083a50a26193f4e426bc72dfdcca59f085aa45aca1c2f9c21e84fed32603d8e0ebf919520bc700dda1f10c8e4f35cefc8db7f8f9ddff38cd55a1f9515068eeff62dbcf7f68dffd7c5ac011f3db0c1631c6fda93d52a7a52d17473f35438f3aac3dfaae305bb4efbe28236fc2d849a567b760143b2593ae018ffbf25e0eeff30c0e2dfb50f2709223db35c0da6702c94a0db2b45d203fc36002358d0632026ac426224078f7b5e508c336edd41c4e451c649e99946f0fb12e3f6ae0477f4ac7b613d55dc7704acd8c5a4bb3079e4be40abb2dad0b5841c1a5e11527c4e0d4ad618ce7c6fb68a23769bf439dba33c3a1d8bace2fcc4ef57cec8c66d0608346b8d2eecc0d130382352015cae352ae5a4e48a418e8b3a4e92b9b8e5eee067f0c9866c36f84548be6960c2c50a428004311bd9ea6c0bf8b05914992a697cd9fe49b86c415f396b3872cc26e3fccca9ec7b36a7a9cacdda02a06a4cfd9da5781075b4ef491943abe11985daa223e7b7f8c6e95e4428ed045d9f7af3f036fd22dbe33859c5e8bd8b59c6dfba66d7dd48e46d7933947f2cf434e671aa3f5a49c9b12481fa99f4bacbf8565e18fd7a21e847204bb7e9b3a54f5f83fa6233160d2432f71cab61fcc8a1768d66b70757d521269448e70e949a471c04906857b3c76ebf6511889bfae9ec1046b5059ff0231a1fc522a93c936799cfb22e1fb014b3bd90ab72315c5645c0c7d7a1d39d60f7f3281c37bd07b9d6a793898e3e42327d7e74cae338a7508d302745e72bdbf51d0765d599c6967573b58738fe9896304760d813fac0319c4aa1e4dff97ae93bc071b56bcfc44e403deeab48a83149fda89cbf79a8e6d67388a8ceba1afbde44192b69bc66fd457cb47dea13449806f6281e8960dd714ce0ffab7a6fe506ef6370b6657b848daf08fdceaa3b7d2dc61cf2fbb94f329ae0c2dcdf061bb73e054ee6f8e9b1744f99919af4e9f13909c51de07c76c60034067336dacf84a89fe618ee94b361141b637950ad40cdde5756239d746282d54412d221801a1eb497a580fd0ca48fc9f66222e1f521dcf85bf4260a1400b8d8bb17bc8471310de30c7e02d1b09a718e8a575187fe57418a0bdadcddb536878782c33e10d1f862dd1a17f032a57bf0b5c398a21db119501aabe2ac4e6e06e3e41aa57a5b3d127d1ea59cde9e7f58821966515e13455bb13f0ae55d32d8eb489880c6bf3390ecd128c6f4149328cc030f2f76dd329a77d38f9827b86dfad9c4dd60303aacc9fd53fccb969fc95c396905e9167da3f5725391a02dbf2f0d24ef27a82c79a6b56706709891670587c135c7c64e60911a390c10e22c299cb5b31bcc0c1076e77232585622f77fc114d46976d5e052216559450ca3454453ed6912d27e53a61d83a9f9d9be40b00f4293f065840c6d436bce470130d7307c4fa86a00859dbeed4f6e521c9a9701fd80f5ece6a73d1ec1fcf9aa21d634be256520ba7a90d6ff1c40fb7d54934c4253442a30720e8655e2890ac3ba369ee53d03af9fde39803aa517fe33526343faac1eff33da8c6def7a282ebe279a874d6efb412c7ac8a13fee2abb1a77129cb622f3272cdac7010d404a5d152eda1d1e8f8ec6ff9eb0f853620ca3be7a95d4d82cdaf6818288457c9abe63064b3a47cdf688be686be403c880ebd3d8c464f92e481643246619de5b10e52fa7ecfc5c7fa6b7964114965e4a08739f5815ccd6851d12d5c14a53cfb4e92e4d8abb3a39f0fd40ceb6a76b8f360c597fe8cc3b80905a2abb93efe902f6fab544653fb8bed6f5bc79d3a0972f00dd1b6301f6c789dd670342e1fe663cc3451c958655ebc2285968fe8d74c60f2c5e39bb87aed0a71a1103a919b8d5e4fc789a5b8838bf38049e08a69e135bad0b386b4089f23d9f5b6dd7d388720d386f0dc234c2d3118db58b5b6ad871febedd169a7cb5eef0df8f30228bc6a060463ddfc8cb2d2d29ee0dab8d89cd54ea580cbacf62de4599bfee15b5fa0e0ddda519021f2e8cd8f02f57cd4f49d8242cae0d8ae2c503eae3dd39e239ea6fa39df63d3e3b0d17f853aa171e844a2323b1a03d1d7763874b20bc080f976da917adaa0b807e2bc432805505e9501a215c1e9336ce5ba4d80c675d99f967192db5f0f5ce7eda0ebc459314130bd5b4ecb936d9284db4bae1c79dbf7306016ff183e32bfc1383b3ce9d1c5a8f49b37f323e7c5ff302d4be786ec16bc65af89204eb0ece1f8e1079451200d970940a82d4621a1e210fde152e214a5e44b7d7864662a8e9113bf588b10da0c572282b893a7016f20d8dd5f9411659b738cc4e615226788028a2a7fa0e58d876a857269af1bbf51b7c38afbeedea8c6b510602d2b68fa8010fa770dfc0299675b246583bc4551614a96bbdce6d775168cb56e1b6e9a7db0910f076ffee2a3e0820fd9ecb8d77bbd6469d1341f9c23199c0d023331ccc29200368c1e9e28325d4f758067bddc9e3b69a48c803275928b89f6a0de783a8fcc2f14324080b21a08ee5d3fe6cf1625b8cd9ac87ce73062d60b40e6b5b54d3500d96d294a1632b01b1193925caf23502071dd930f17f7d84f0a89813aaf1b03aa37ee2d298565601058f7d9d1f1778069ded9f7f9796c91e145164fd66e5d0d4cb52a3b5dddc29f0c170b36bed4e43cadb2a993ddbc8c24951561d1d4791b577677026b545debf64091ccdf5dbf23fe9d7687c72950e6f87ea3984bf62ba2b15c83562097f1367c70bb1f6b0e3c7ca65858c0fa77666a5da3875616212c2ecc5f54cbb5f800e27a062902adc397ea8f9862fc6de1d0f4315bd92664665a28751ea0b6df07cb086f1379d9ba8d1a88c921fdfb36f57ae42ec7a952ebb59aa169ec8c50cccf917f523836f99407f210b92e36724b6d2606980325a5e49a1ec06b86b975b7cf05b4b227c3b9eb2956f740894d39849dfdf6db759b466394764464a3bbb562e000390e0c63c1680494258d4e75d85313d15b4763732b162e83c6b3a1cba7b3926327ba2048654fd604a12898881022279d9032d96e87e4b0bc9300a3be2d0a053774f05206932d25ab46b7884bac3bd439463ff14412cc44c91d06854461db1119b6c8e56df62dc4aecc7beb1e7170752ecb1f1c2e2d90711671db0af3a590e3eb88deacbd424c6ede8f075997a1d014afbf08b12c03a1a81b22e4bc18a3f4ce3ca02cb5925921d46febd5649b12aedb18fcd476d59f64e374dabafd518eadb0e8e9f7e7df68c103eafb4831a5c125b77a7870be169d1452d78823489907a00fb84b57077f3d3cd5c6f0b42dac2952e1ddf6ead9899b35af094bd9741dcecee19f5f82cc40e568404bc3bdd6f78aa9a3c9fcf1c31cacd714a832d813bed58d78e7bc839cb5f9a30c0ca6c0a78f9c849710fa09309d2210e02d99395c4a4c9b7d55cff8e5d21844f38700ef30b6dc827394375c8e109dd69433537da13c5a6582c2d3f7c314e38449d8fa236be62310bfe60b6a5b660fd4e30f3da2e979d19ccf35dea106b4de0fc7ad25e7a3c1ea7b29f0f84f12ce6125420c1cf00a7f0bf9ad9731de62b4cffa5d303f56bdf9e4745cf7322e01da2968c157283cb6e268f4ef6b92d0ff751a64a04356c8e9c260cd92194956b3a0ae58e4978282a52f4fae5573a314d061b434c14ccb97d7434869f31fb812b2b0aa58a803a9ab46775d07981311e1a9b379bebea63fc36175ff51fbc9a1b329335e6a2ffcff15be8a23d6fe6f5527d8e1131ea434b26fe8e0483dd8fa0514b6a88cad55eafd232157263b1c5a73c5e90d9e8db81a56e92a5c6b79e9244b11e2063087f714fd7be73dbd1926bc7b4fbfe5d5a66cf9595a03f3a82b90934bd9cced0bdf3e47d36ff8d77e88ae91180711cf3cba05ac23d33f99b5cfc0a2461d382054ffe2bf09840cd24b03fee9b62d1cb0665f8decfa0c34a5851cc36afff223c3f5d2b6ac785679ce8393e14a3bedd1bc34a232c82bbef7e4699f06c848ef14b5d25e818ccc364c593307c24ab982e59ed33c26d9624086ce5192e37280be91ec3271eed9f5041a8171775a52b0eef2173b375c53cea85a2ce0c8e54ed8d86c2c38e0600958c12b109e34237e4f212879329e964d6c230569c9a542aae355a5aa42f7358096c50c9e0f370275d03ad9a4dcfbef9e0f3afe6336d6523315610824367c407cfe67c26b5a6d556812cc46fdc36b466c86a5f5026cad4b8c6cc3426bdc62803870eb0bbd1ff74cfc5676e97613362844237fb4a69a4e51be01c4892cb170af04f206fd8e24679769dcd13615d16c9a0f50a7907dc98f2a5d2d9cf5a2c2678f80574198628b1156b8765f2bd7a5ad377cd2681fd8b063394b0dd595491f10bb3724f5adf0ae7f9b3d8dbee8e89dcb5fe4552d39fab6460c2bb61af756dbb145f35b51cf739a8a068fe5e239f7c9a5613d94007e767878d256cc44b1518fc7d22863d9d65c09e09d7644485a6f2cc7c08cb9ec25279d7c95b0a5a4eab020eb5f688b6a9043aaf6cfd02df4e72f6314bef4064e4c9fbe25840ccd60ade09d5c07f4e775a12526dda737642c3084365b6fdcee47b9767aa3969c6fcb0f758ed3d406d3a9b708277eea1bb2f764cc7dd6959e4bf10eaa4763ee3588c70cf8a68493b8b16d43b75a0dc4f9582ef242083034b3e59c4e0dfa1cad15e515191f861a894c00b64810a296fe2e295a95c4739439845524e3218cdc9e5f3a9e03dd28bb92b56a6f4c2eed4c96188e17b6bcae0148d4430127b3e40bcc5fbca77ea546265b50b871070c1cfac4e3a1af2205197d65beb406e9b4a6f07de28c221ac9882a3cabd700b449b0c22aa9271c32dde7ffaef4654a19e78089ba18ce9d1cd1c9317c2ca52394a7ca296f76be5ba58ec8e6344f456ffffe47d00fb8de14c2f9b33f4e017dfca6971aef8562dc4e397fae99de36da25f0f18fcfb9cb7f8f1c03343736ed91c6c675d34dad7226294d1a0f2129b425a7b779f1bcdf3431e9ec6ed55255e8df402225b4946a22f87bf9450421bb4b62fc7e4691ced5f6600c04f7c5a5714f33c06e8ddb8035f2e6d5d1118e05400e8af1a24a426190e351d9de323c9a2e60eed611a42b0427f82080371f0e75bb5f0b87ac03763cba41c8e406735dede6aee3cdf829ac1da19591940a0680c53127bb3c2cb7135c259a1a03c4c127d3c1138a72a7b3aa3138cfee091f8b31798800ed68c046a985db4a659743780a67d29d65f3f67fd105e9527f62da85a69d4e98a17ffe01c9b81936b3ab5bef0502763dff4869c829cf529ea8bf17b38e3fe1713a63e8458db80b7960f54657b70b495c58463018ef2f1534d761337e90a7e4ca31d6a2482d4dc3d3f0f102efcbb3631ad29c7c5282c61d09b0d1bd94e252c6491d66d072c42893f6af6ce4a7cf466a75bc81ff5455f6e9a2d0589ff6eef3c15663fab177d71fd2ed767c955d2211f67b55845838f52ddd56a9ab21b46c5321b884e92b7590a01e485018eda18935402c227098152cf0ebc6eb223143ae15f94dad6a000c6fe3b3e3ccedfa4a07351fa35df5484e41575d1a2a0c3a268000f5ae26a1ef421c7116a69e873d63c3a3790740fd1926d5961f8b871b19abcd73e953099ba66e05a4211849785556af843df30d1f12ed23c16870e1045c8759d08d236c6bce868978de6bf5ee511f4b841fc1a0bb2581538fe13e9cd05c187e452fc37776258918f87e5cc70c59dbfcce83b2d6fbebd7faff5b8cb4028786209819df388524924751b73074febdc0d22d81efa20f367c3083ede128fb9408250917fdc8f800dfd31a86ae71bde8cc71f0dbde7f7dc1433d2346283e55c36de1eab92255e25f588d6c94806056ac97598dfe5aa7a3690f84e32272eade97f6decf3a51540c5746f113816063b5bbe31e8b0f473f9ef7abf57bc10a436ad75bd8b5bc0aa0386171004322abe284f5b96801f6a5784c42e8ac90ce528a6813ff83f2d6de57289b0a2f0516658e32c2a1409a909c2a827825a07dab7f64005895e6f1d77915d9aab93bf9bedb49fa971e46109aafa83797a74a957652cd1956e33762092c41c25a250e9e2bacf7761bbc8ecf64a48cac99577b0c7e087a880b0827e20ba141db451de9da6e0a32b5f883dfc0e6573bf4e3b75737c65f7099976b79d5f27d702335e7d9ebdb06ae48d39b0724beb2d418428e939ab7e3529c9b10f03e9fe7a75d4eb9d6102776e2725628bf39e9c22b6c8bf592de0c0e7d0b4b60d78b33f309acc2762c71b0d2a9a8de8c3f457931294e51a939eb961a17e69c56b5bba194dee1b44a4ac0032a0620c4a493574008cc5111c612be00ad4486a3363f4c6f8d98eb694da75ba6331202afc307dfb236f854a6c48f214b5793afbe8d4b3090d8e0a995c594ba325a0806b5526bc04316e34c697758f59d18c58e78732755a6c2d7f9e99adad47511f6cdae6d3e969c7805b8d851fb883a42b2bd28bd153ba6955434473e4773f597872b71f3c7c7cd3a0efa5f66f40d5b2d4aa574c36ecfa401af366e3bb3e1f1b64e6b12f470809a63b785260331406d163a84defbd0e0dbe3223d2b15977a413c8e182cbb6ea0f6b1e260e28e1b4a92eb271a09dd06882d2d294e0ca9f9d6b969bb8ff27d26815e7cb9206c9119b586d5e6932d8cc5d444517d7ea4385a5585cd6a82d7920e24dd8f18fad7427eea0f7883e75d9613b75c52aad0b51feb2a2e7c762bc5f64e3d75327078ae913920730d281259ce265aed6be4bec14a30f233392ba261791794581470442e5584dc7980a1e748abc5d3443404f047cba4b6d6f929c4e27001fe8c44c4da26e72506de9e193300c7e989dccdc8830d5d903d6817c5897dea46b8cd8ce9162724e56b0b377fa70869c76165479b03bda409e848f2ccdf51a9e33b599843a73d15dd4f3ed0bf35708b72081422047d2adff9e842fd78ce5decdb1b84dc2119fe3dd1042deab24c61e7b8bf75630b76b3625d3f301758b084c337d6a400e2e6774f23232dc634ef25d0698b78456d1036d0ee939747ef5f237622d2033a1590f8412e664e063c47349a5c9e7267aee7ef7be8e298e442b6d4d081f9988ccab61d0b655bd4e2757a98c584ae70f635d1a1a2b03e33f8662b8f3002805167b63ee0182226cf44f2fec47d2d7eeb3665dc00af730e431906d9e286828a492ac1a7334680f572a5793bcd60e949955a0b802156f2956babd76f9f196f35038a90cb8c5b4f1089b50007da6344d1969f9fbd6e426be4d4a001196fbb8d127fc4644bbb06d756198b29c9b2dc3a75339391cef27f9f5326721205be0c4cc254906dddacfdd420f7a845a5ec469c5ef74c18f7db77c244200c3248009ca80c6dead8e2002260b4119d3cf15c0e6c038c566735b54fce7b8326feb7d8af3abecc7ff9af6fbca20e46de91cdc135426f1c00578bd0ce3ee62c4a3364d68aa5db2e29b69f92818759d9a267828707c193b285e47fc62ef1a3cf8a6acdf81054658b7e75073878597baedbd7af83e1c934ae581e50a58154c6b2785d2eb9b186d8ae4d47b01a37d9e16af4faae796ea5d92fe79f9782c509ba8c14efd4d8fd924cc18eae8ca8231a5028725318922d3497790a8dfcc4d7bd629ae82400f19a233df09ba9332207a2551c26edc82fc076a010280dd4bdff988402fcab34c90459eed66e85621628cf6b1046b103466c9d678d845e2280d58bdc3722334e7d59d6fd05f727f96627cab2ce289601c19cc803533168f3705e62d0ec9f23ff58ddb3ab49c5f39d49efa493a38f7e5b932f4d2d2489cc9027f1cf86735527de6df21aa1e700dd41d52824925182d34e31ad806c07fb3a9149ad6871a29bf6cdac4da2d196987b986c932aa5eb39a903a2b78c7224a4b88a560683bdec83bf5fcf7c24c49da1762b530ffa83d43a5ca000b0d1baa8945423cf40f6875836e3f8d3b2e2ef3ec2f9cca5dc802796ff04e262ac03c885211b30a8dbdbe7905ba927d7a49e58a909a97e14bc18c4fc49b90e123f264885315457fd2e9aa105be2a996aba3f5404c958eba8a27777f24455b9a953262ead9f3c0fc1e77ed5e9e0544fd18bf3435f6b49b22df2d35f8fc0776bbb789b7831f2e78a2dc8f9f454a6844665dfa86d9ce437083033e75802469f6eee79bd9d07a8bbb0ab52e531a0a0ce4f9c2fae60256c14ebd789df9748739b3e061a5e72aa9380d6937761328167dcc0947b0655f7f1d958eb8eaf6beda562fdecddabccf2a7160d20cc49de878ded7d7a3063ef94fd742cafb0ed57905b33bcc7f265049dcbc33b8be474d8322222d385295b5dfa540a625cb49589388e6c5726b85dde5df9e1fe137041a66bbee79f5c0b1adf446047d60b3fbaf1b0075dbf2a6983cd3d850d1a6e789e2e165aaf2aa78c05786f67e2eb8c3027fef0b3eaf8fddf4831d372c722c3cf32c0d1791914739d56ba3caab105cf848313a986e9911bcd427dfb5d8139c79edc91a88a521e099f5b171aa9648793265e71381b44afa85d5d24037bcc453bfbd824cd9b10a7fa8cc7ab30a8903eb1f04375accb1d1dbd99a21f3385fb35a7942ba3d1106b8429aafb5fce145b37f0b4c80ebf93fa78a4fdb0deda781cdaeea718f2ea83eab9345ccc2457e574ba26ede0e90f2e889e2f31d0e419bffd7050fb841343fa9f2e4e54b39ea2bd93ef065bfe979a35a6204c8939062f3f89b3fc4dfd54413cfe523cf28b6b8082bc12439715da06ff12c2e0bb1977dac74397436ff61af631659980b389c25f9fda1aea174ec9de515e2b149cda581a1872163e537e6d375e0c3e45159bce68df90a85b6c04b518de9f980702138cb571dcf6c7566923d8408b699a97aa0b3f706ea5d6a1d8e127bf8902ef71548eac2e3cfb82bcae5f484c859781ef1e7e8e4196fa5f636bdfc83e97db2ea74d9c4b61fd337a72b6f21012bcc009581cd5a27f976a420fc9345e2fc5a91c1ee778b161a7eb02e5a491d05edf3daf8d5cbb18d9ec1a25b2d66d63752115759841d3db60ba273b4841d1d00b87f8e0f6b12fc0941f76acfa7f34c83fc507920dfb0e49c1b6ab60e244c7a7dd56325ccf1e0a9657369325fa2ede1f9f264bff5d4fbf07b1422077bf808bd96dc94d0663d86ce1ebe5b011769752b94ed9c9c72c2a821b893aafd194ddf77f7dab77f2ced1acbac177e41df2c121416986ca8c7323b0d7d33d99d9d7ad7a8f9b367512e7e10e136fc51fa0082c9c251b9c8910cd1477de272934495f513a525baf5af8168b9ec2299af3965a83ba9ebca5cd8ec5b2dc9bbb24061cb9bae23a59a90f3d329c2b4c527902616cf8dabb39d7c393c86cde23efa4c48a1e427c639c186799aea4cf2c69f39586b9bac23cb09b9bf1cff7a048b10ed4f729d5227a5087b3a15f0ced7f3a15f8c26322828e1ae68fe1c2394fbef6fab1c0343fbcb7e569807ad711ca41c3db24aec87cd63ae5f01bd4ba68d7fb606097e7dfe08b56c71c5dca6844770dafcc017c33a07b6ad3d0e48fa53e21fab03f42edec755a10f48b608396628373a979d2c5d613d1e181e6f1c03cd19309eef59c2558f807b63e6b8cc0a21bdaa23fb8e771145785cd493bd7c9f0c7f41184a02e66ffd15ade6695f496d1b9b25e44d9fbe9749471f4d906dfb39cfa82920cfe336c7d58020f291186931c6d486cb20fcf6fa43d6c5ecaf688af2f0dc5980ff13406cc721e9b943d22383685d5a68b9150f10b5f89151aa1f9946a8a1a257a49762e7d329b1a383bfcd9cf1f42af7966d24ee55b093242e5fd5d932fe0b54a3633b014444679602e1967ec145b88a0136d5d4d78d347a5b4ae833afe333b917245615f24cf77e5731ec37ebfc428241b194715fe72dff09e2796b35029a78656f4ac13ae5666cf4151c00895938be25626ef747c9265e0394689edc84bd99b8b571c1102a04bdc7c98e4d28d0f58a1ac1e63890a2494bb95ca1c07fa71b9430c71ecaa6583f942542b5aa75d48d6ff85edb6c06e653e109da8d3c91c5ae7e709109e1128d80368493f000c4d50997edcc1b91db0bb120f3e8c8780444097e0bdf77e2642c7e1050d4c1819fd19a2f7c0208729479a9f292ac7bcedc9d0f69f10a0c962147e17cab95799c7fb8f2c46dcf75f59681e726cc1bfd5f3aa916544cdf77c8b3258e5d390d2be70c9a1d844a98d20298c09c4c97c3ebdb5ef83cf9a3083044fa6f960fbe2a4278836e5e0aa42ba06d2649da65289587602ff5f77ea3888009dab36bc0eb3db25406fea7b165fc34211646ef126589cad4e601c38f309857c36cf52b60dbf6d97b32e2f8b16d587660df550602afc86da10244a9aee98d9134bc70a1302909e9a611abdf0ced6bedfc1c6321a32c279d5cca4e9168fe0915f1eeedbcdef00f3cffa94bba7695aa3300c6ab03d6ccb1852290615412ab047404c089a38ab4ca17414cb273f2e456ffd26fa1661ced64cbacc54c09e578bfdbbc1c56aec179ea052bfe32a27c325e3fe8f97f5b2ff20a4461df4b8ac6ad0d01f4b14e3d8cf606abb36e9f84e748c133e5c89995a87da6057c336d5c4305303b8e70b5df6fdd7ba6a2d87b7ff1b63799ac6a9d17d454aa836858aff32973c3387556ebc61db59e5fc90332f3d5d01d89bb51fba05d6d8e38d40873be5885023ec30276dbcd56d3c3f3d8a4e682c1fa62a7489f15bd75b6cef7fc2f69ff212e276586d2f21effb7f32d9744f47db27eb7b9ad09af08719ed873c73790d31738e87d27ae0c3a30d3317118e011362f3fa37344f4e33320e599722ddf2c23d42b779e678d9111a7b26480c077784f67f62ac7c99cb27b6a012d3f496cb073b9c8ba51370035037e29f7f67b4f00a36a3335d0a67500f4d97ed976df5521e4c539c4df630f5556b91f508954d74ac157ce6b75d9068488a85c309054fa70a0115acfcc65528ad30b0fb84397c75184a8c7a40667e1ae4fdbb71b041958d5b5f8b1486e2fe7aac2543749d786f53c9238ce92944ec3e48a503a7bf01f8d813990dcf52352cd63a3c749f1b3792157705073b5ab1f0a31f4bb7ff32032e555b51227fb786ff079dd29c3b20b5e02417434f44c49faee1ddb2d3bb1003d3737bc12e01af6d4788a035ce1e5315a76752987a160db0ea4bc52d0bc98aba838305d1ff761791d2fb00b1233f0474f0e51d0d89f47a92578fe6fb1b3d490adf0b1771ec6a67fa4715823e9bb3e9a1bfefdf25802135dd2a79427dd51a19d312bcc4ef2b506d74bd5021699b798f1324eb61d2f12d48b3d89e25800f59101a7d48d028cf1fe3c78013ba0e71d4d2829c1568f9d08286e9879b29f4a3c8f6fd268a67072aea2d639e28e2567ff8b97384577a2eb021a00a9c447f37f92a82c01531b2316cbbe2f2fad6adfcc9cb0110cbaf048fdca641c5d18ceba05afa30f7838faea0dc7cdcfe1ccfb815fa8804bd0d592145d1953497ea7b1bf812a50bd728173e689aaccadbe07ce58ecbd3588412bdf6f2cd933d08371471ba09ed20c5946bd04b5d9e0e59966cd70c4258c78ac8ae6471e46296da1f76858f1faeb5a37e76f0a6db232e8096adff9c73e188b0507c1fc93816e8c1e6c2d80246f3c87dcd8e5d67645a15ef06d3e88f147dd31239d6ae7b5719c5f8ad5ca77b318c8911addaa11bd98227529fd53bb10ed805fccadca46ce46fc6975a8d72b5968a08606d9ab50b39371a676373a297e33abca204c91cbe104c0d353fbb13148cf51316318cc2589ee49e9fea768f9976ee0f6b9cfa1fe23ae9492f0f5be370bc1c345f4984437896d5a10d4272be0206dfbdf64949397cce19d59a051ca128d9348ac1607ba0ecdafab8b32a42b0393d5bcfd430b1c07097c54238ab171cdd61180fbe2515011b94ccb678633a0f4a15825cfd52d83392e980a983b5f1f20a9ae90fd5f522c88857f5c163d98935cad1f591e320ed44cf69db8f3124fc64cbf5f2a6e1a9f00d2f7a09a73e73be507320183c5c8977ba814ea1d658cbdf114fd3daf9b2002b9fb6d1f42b68e50202a8242590bdb966454a8647b72f6c50f38c8a23ee8d9c19cbc90b8c595be6b6968d6420f3bc178b9e38bfb6dc7f74910f5c4136a0ec4509a7772edf29bec4087bae783d79e32f322a52ffad1d7f383343bddb12512ed8fa57df751ef8884fb5b40664cb0bf4258d3ffff26e6653f5dcea66526d190c5a46718955e3d7ce4f756857445697fd2661d200a8918af9ade92c635c3b23bb50586be485b4431bd4be84e5e4167b04cdfad7a4af5aefe59568be84896d8f90ff5768c91d2c9fb001a6c59cb5697909b9fe46b60a111bc038f1a5abc8861466793c663e6a2df9e8c00ad58e43306ffe10fbcff6a3d2e8862b57a083fd2ed09d283c88caab7fa1e1189ad0be6fb97f090701b8be67872ac319d75fffae702ebd659f212b95c93ba1d6162d2351f5da5c6aee3fa01b363f164e9fc1d525681465385790505852fa411f22baa23b17eb71ebdaf5fc1f773d9dabace2d43f5dc4be56892319f293223d611a32ebfbbbba9a6f36876a619c3b9843927613b13dd817225b88aae0a1fd5c59af7778933b23cfba5d598f5f79ef829a9c02cd64fc3dc2a3da080647c1d5ef31bd9f005c05a73c8a96cc2ec7f0af0d4a15d4e907d063065ec6fcb73adcbb2054b24cda92aa25d137b6828a65cd79fb484ce992566ebe43bf5b8a4c43732cdb36ec2c5ca6422be8e8a1a1bbd388a5679a33cd9c23683e2120bf67ef9a042737809e6a43754884d88d76bcd22dd31e6269974eb62875b49f3e68b0f0551d835ca80c8a3e2a4d5259d8d5a8713bb5733363aae1cdea0c31d1a10b7e057f02f1517ab9812ffc0da1b5caf3bd36a89f93bb0d9f1fa26859291755390c38a13e681aa58b9f894953bfe6f6f5ea92c24f8e6deb2f3234797d73202edd1055774b51a0486a76b7b5a91134464a2164f6595ca5a7a50b0989aaf7fbafaad5c954d42d50373654406aafca564125530aa72d8c06e9c7338e1b0469ee4f9265b121441ff2f2db8ff46ce21a8376a578e31572d974b801f6a5d8ab158e558ec70da1d91d4fc8c41e3fe6016163181062848614cebd670f7f59c64fab0658e04ddd95b77127e8391c25260572519881ceae49299963479fe0fda28a408da537b78647bbeeddcebbd7bc9bd599b084ca09d35df75daee7bb8b5ad5c05ae2057cd9b07de1a3a2f4b73f3be49e1c8310bb8948d80b6886312d131142f3f2e24730a395f4ecd643e19f727c52a5934b460ba50b0365c6f87a68beab632996add77af048d0b7f62cc28486a729aa4aebb4bb1263662fda19a357039dfeab6e3098e96d8bd116e80f1ac4ea477abde023102fd5310c03dff9656a23bab69659fb54676f913e7e2c162f2956a6be96797f57266fa3beaf39ac3160f49a67986c99c19884f105479a749a0b10c633ff9acd7181f51d1a272b23af4aebf67a71de23a1fac5dc81192c39572bd24296c2ecf7e759ff0b68a9d25311d815cf1ad7fe5e508dadd1f62a03fbbc7343d4ac1c1a2da76dd765630ca596bb0e48e85ef33ff6b9e60f129b662a1d9bef9f768290ed8f52a52508f4675e6162907b7d420df7a04d64b91cc4cae574d43c7ef5513bfc10f37f270ef12309d599ece655f844d422f9240febb57b580ed3d16111156d97c8c27d6880734a8d5dc5fdde664e91f3e65ed8ea0a47448b0cdc52bff59ecd0a2cdab852ad284f83798afbf85583f2d14547d10564067463121934fba1ae6ff6dc5f3932b9eb94bb34c1d50272159109b173f25521eb95876b37ee1f4c7e9082562ee2359f9be202511ef2285e600513c9c6fd0de6bdf8b361147fae5715abd3056d2b14b6436c4351f327ff464bb2de28742330f02b127eb3533c5ecb0d5240d22ea668e6a8cb7847736fcb623d24fbff993a1189da8a11a7ab57be1db91ab83acde105deaa9e11e8e33366ed5f46b6ba38a51b50d36220fcdc6da0aeabac6af9dbf70934b994c0d6db8bf8109699e7e854860f95453505162635f515253e7687365187c210ecb3a32067fdd5e324f4a9fae3e424b5792fe3ddf08b45a575d9206e5eaa5fa83b559c500cad0c099616c282bb0c153f1a423e40c50df999d21206b498ba8f8bed697ef3a4931644afc1e7881431ffb614daca23892d944c0433327af912f224dc1fa820b4e3ca53524d2cb0291725e062178b8c4f9b4724bfccdabb2ad058fc1cf6d97ca997d7d58372c95496dcb340c719fec5630bda0d0ff468b502f1042f55bc47f7494a5cc8ae9ef02d2240ebeb15fea475fef97fe41f928f6ee7a953aceec9f072e6cc1086174e623d34fb586c6abccb9c0f1c68a3f01ac65bf674807ff1e19f51d8b0427f5e26ebe8b27c2aabf05940d929dd3f803da24336dc53321ffb791aec0d66c464136cc34055fd1a0b2c391cf812c036db507cbf302b2bdf422ab8746f925bb522aef69f04049f855084066f9871d3494ff3ba2ffd4f1e23c7bfbb9dbb060dcf2f4e710c4d5b6dcb5cfd816e166f91e865459cb68be9104748bd1cc332879ed6da53d1cc0810a528f1ab1e0dceb3a66e96d0ab8fca89cccfeb724557e05251f85dc0ad0d21da5e4e298e9f26e592a0fce473605c33ab0b4a9a4c6186de7bf98623f6deba9a68b8147b3b3219528a8782a8ad42bfa43866c1ac1d7ccf7bc457ded7327e636d8e3407d7b0f513d6f0ef09528f5d5e0348d2b1ddf058884ea2ec455eff2f4831318ac31c64bf50ecda944efe1b9d2093a643447dfe8283d8745a9ad7754e2a3bc2c928e149b16866006517d1c8abfb7b2dcdaddd7c15d069ee832f121ab363df58d7a10c41705cf820dc342e67eec7f0ba87960bc73b71e824e8e7b18abfa853a2c1cc6fd1075933804d0220bfa71a00277095d59e029b80397ccec7df11820b08f2e2bc7728f8b73550a74065bb32cfc39a250ccabef34e48c044b0bcf1c0ce9cf4284e4fae75fa7ecffdf52ea02ff47bb8a1af32e91b30664c1fed60eadc0251d2edabefb9380059564e8ce4f6e4fc639d84f83ab333a85f8a3e584be9e833ab65d7e5206874aa9b08e2de8c23dc7f7aa1ddf6512ceff1690e25a07813d8672e24201e497bbdb24be01722d9d541588de3684ea8abe206320be66770a9be8758bd6055c36f21642ef0cdb6fa9465cf4d1582383292fa85ac9f00d0ef74f265c73bfc03cafe785936203bcb9f5761dc5f56fb721de059d62f0745c260d67e6561c74832b8747d9301a41826b2236c4173757482627ea5515a4ecac09bb3f89c2dbdd39972858b9195a4ffb98b7f0752637a94994b73cc672124194a05a85fc94dede66e821106c89bad49cd86931fc8c86133d8c6e1f1b7c0d1f1058c473f6869371c63f3c2014bb8c3bc2bac134eea4690fc64f862b21aec40b1d205b2390c5404385f38b03e9d40ac6c38c3e2787d2985be67e567850903cd59ebd1f4a60c52f74497259128f8fc8bca7dfadf74dfa3b7054d6da68114c6c16ca3f821ac2448dfdbdf5edc021c48c5b705e4c7978beb8f784b103c796292fbf34ca0247d1930c7d7f0c18792ff47100c70771e0270cc00b74ba29bc81cb6abfc0d831986073708068758048dd967a0316edd7f60ea82b9e49fdcb02032dfb336d9de5246c996be826cbbb05970ee0db2718db57fa405fb842e87015011aa39e8d14d0ca452701594b45099b3bf5052771bc42c583e7d7478b501521695e0dd3116d452246db25fce040cd9bded12ad773425725fcf2d5aba2552663c6db90b4d445e3bd089ac9c7eed472293d730fcc10293f322433ad965858120a2bcb23e3425d23b27bd26eaaf37c8822fe47bb15777e46337aa4adfb24af6f110d29689acd873e1d9653ac292cc0ec8a78947c170ce92cb0aad6827e8b123b438668cbf3983aa06544f8c438bf1325d380ed162f68b17880d746ac9227a86d3aaceb3220de830d37848ca16a2358d1a6e303ba2266d6ae5371ba6ca2f8da9308b69326508e5cc22139e154af7bde2c3f38cebb129fb915d0738591ce851cc4a4e6a47b90fbb9c75865ef1f4b6e7ecc2a3baaa9b9113909a4e7ed5fd0949ea4fdd08a72f63f02fe18a897e4f5c5b019d471092df46b3bc738e6743c5ecd2674fa1265a19e0f26f53355af225150b95426d75059821a474daa45152824099e0a116dc98d8d928a2183ef014be168b92f57fdf152d0f665ff9ade95c952d8ea22c14a281fc63bf03add9e29049f78da2bf8724a5d22b37a0316010ef98572126e10faaea41f01fb28799d74f1cbaa683274b87adf771b873d42371df37ae5f5eb6e4c734d9d91002bb8bf3fa790296e302eb79411fbda0bad369d6d6548c2a07290b25136df54ce11ddb394d21cf5cac11dd308b29cedb5819b99a81c154e923fc9919fd0cbb2fb3a303fe271c8e5571b6e4c3d0958f9406fd115329ebdb4245c57d2f0f49a84fcba11e9f12cddfa7a19ee9809aba801613c69af01901d4683da39fb6d18b7c8a327f00adb15030a09c6afa6a96603d2dbed2f6264289af6cbb31d308da9f18f57a26ed22c54ed63509b988d48bfa29cbfc3860f8a9487979b0e4bf69d767f49afd0dd7d331ad10a4cc31a2d9ffa50592fc38a32d050583c1ba811fb4db314825c9418ea08753c87fa47e8fb66a35b0cd632c4ceeaf807ce9119ad481982477be89a8943f95fa0e745b82be188d1e17a1822e8a760ff33d6faac7f05cea522d97f04a1a2d3a9fcc3cc27792f12d86a4d3ff094de99e1dd61118f786ed70fe73f104279d9a4ac3b786e58a293e363d520796b58116917b3d528423218909dcd8696d3ee0ee5670cd4043b0ef26b6196b0bc17af6262b32412c48ecde0053b1f9e73112fdec680337e6544fd6a40056da07a43cf633b05514b3b9727fe29bdc60b6cd94b599db6071b349b758e8d260d2ac1e9be7ff40f27e523b8edaae92ecfbdc99fdac70f82adf22f4e1b6028a7603dc6d881807932e1e6077a7a3d18fcab534d13e2bed6737f0dd936103e852422a8238d560ab006e2b77dc08514bc707e166eeb0357c156a1c761f7c2c232cf0228e69e013e7c4b75d106b994942c26865a1af287a369952e3903e7fd29e1b982fc9b64546b2b7536dc6237f8c186ba7c11ed5509e43449c123c2afa2f27a7f4df2ad2834c268b3b7b62be50386cc4f8482625570ca64f69419789a9f4f16c56e6621507296e46738efe1ff6d354930d385ea5f92cc7eb2795ad27de61c56607f8a60fbaa0daee29c923f5465647de4f4b52dc624419fd84bada582a4cfe230db574510c0557e160b23aeef47604c3b52f70b7f4636ffd74c66a6b24c4da1b9dda6a71fa91ecbd71d29517e264a970b365c8bf20ad35fede454e5cdfd87fb1d8f17ec90b5e053bc3be783b71889a1c10dc89646ffe6de3970294f9ef21110e173a37d173cb49dd6ab223a1a5b1b3478ad3298331fd56b8287a7b2ff739cef040409a15ae3968bf8e00b5953d28a039f97c80be273e897dfb9dce73930fa8c09c9fe08580a6e227c08ac26f978d09e92497422b5a74e140135f83cc9cd64b3e3f63bd6b5db05eb965c3217c5f115352b16a72e79465dda875545c4773d9fc0335d208f3fa791cd2a1e675671483c60b4f5f613f09d0c922107df7a2a36319c0283196e9999e8b70b34eb997fc69bfd5ced90c815f923a136f29dfd183432f76a57297db35b3ca55b289444d6b28a0178bf8183b3f211aeeca645137d855f4e07ebb8663173451043d0789e7462780a054562945937699f009ca5ac1d6e1a3849e01b45ef07f83dd947bd882d16536a721a68ce5117b81e5b3ed75bbf4d13a0712fa0b7460bf129dc47588045686b353adcf64c69ea8e7d6d6f47f60fb6f3f4bfa6869f5166940e5ec4375d8f3b83af7b1ff943fcf92056d4d9d6e067a2b5b60ce012c7cdf79ae984369fb4d8471fe0d2c6236d025705eabb480f6ef0bb014f0f39627cb814edf4d72f92a6467cac0989e09f8e6d5ada021f040580f0a1833659eed7a80349d02f4c19a788d4bd766727961f36b6429dc39bde41793e15b5a66da0396079bb4f15d9342fffdff4c062546bd03c84bd81c78641388ed561ba1b937bc5bf82a9b34b29b745ab3faa9be4734698a5563ddf681bd834b2f53953bd5ff81f0d2a6f72fb4e1eadd55181b34826a1be2911f27f5ac9c0878580987573ec9a1a6179918d672b9e394b6ff65084faf83d5f195e5d4de6cbb54c3e7a0b10d7f747c285ea29d5b70aa29fe43372d398846b6ae36e6c7fc5ace184e7b67f377d8881b628bb6169db9f92d51ee191ce451cc5d14f8de1542b99a81c91a77a2404d007277f726fa84f28960e31324acb41b7bac4f5206a7fb6dbfedccb3e3aaecc232fd41a17044b92b2c0153020b9c08928a40c3c063fccedc56af4699ea8e591e210ed8d16a098b73c43c5d205939f9a655b9e85aa30908d3192d3668d1921136f802fef172616079a8b66c6fe72000296f0cf1bba4ed8ef93b0d2c212628f672d734645d36950d887843751b731018d46a3d13ed5e0308fb77ee561e8dbca22f52308308f14f137dad6465091d12bd13270dfc6eabe670be9074993bca7d75a796f3cdda7ae8275e02cd50698c37c6bf47c7116791b35d4f64ac899a17a8ebdf32ea6f200202eea216d9f2ee8a412da593bcfc1633d5e6af9dacff80da1ac0936f51511cf04a614789b924f004aa481822aa0dce563c23f05f74ad6e57816fede152e3e03a1d72b359baf16049a83eb38538db67ff9fd835a51414794e16dbd86ab2e7040d964d8cf16dbb94a028bc66d0538d25ac17260a8d2e03ee824ae9605306e588e25a779d9f07beb430ee5e72d8e4ff45fd8da6846a0c934eec3c7964b26947a677405534f1518bc5a165de2efeb45168cd1c8744a975360eae041d958aa9e97c9ee6a1f81f3b154421b33e4e3151155d82477dd0de6fae0fb41bbc63ca318f6a6b2802afec9c63d22d770bddd9a70d64665e584c7b7d7f77b0bf312af7d404456537d9e768b205178e5c16e633c78480084227759b0bed18e8e919f5fda006179ebbc4924f967c3e44018b52df3d637c521787bcc8dd52d8631a7d2f371214955dbb53a6ac3ac20bb5979fa2db2dc2c3ecd42f870e4cc05ec5394767631ff961dadc8b83dded7a6466ac376fa5a50d54c1fb597b3487a52e14d369ed0c81bd1cda6f10b1235369b4bd4795ee4ac3ded8a871618ace028d65bb0a7c017cfbd4b473e1554e0e5a1a10093a0346e2953a9a1ecb80c92df91f18caf2907331cb5ec73ab334fe18cf10acb99b87eeba598a0a2f230ca35f390abfbe1f009823a016b5a6b98e4e73d6626ec71a59128fdf68c7e408e0402af71d71149144a28cf21fe40f6a0fb9bd261dc0ae17f4de25e585e64784dbe0ef740d6003881a9d425fcbf0998a87b88980bce9cb0c673750c62d2b2ea087a0c3eba7f5cf435d2c528f2492820e829ec6ffdf16ff7122b1b51c7dd261039654e45de454d90de5b42649b705c4dba314da9135ea2f85239e661a2570a5592ddea58e420fb478b1cf8bc65ba0d6fa920ccb2827bddbcdd07327efd50fc97b31de81800f421520f6d891593945c6be9a2a15ac4d39f8c7762f34958bd885267ae750376f0fb8fcd4a0ef40d729ea6786944849a558bb8a2c1b58115020f83338852fc6dae5b7601997337b6ee5f67836af32049e794dd0feb19d124245d4faf325bdd0c95bbc4e0d673b2b3c8906dc8a79c2cc9911e3562e401150117952cfd660ab05e9df7f5d5b6ffb566d4d05d54a9a4482b1c477ebbeccf78df11cce8136a486f5f98c30912401513e12e0fb075af7f3f5583919f5d069cc606c83abb31c8a7a1906f336c02963a368f3c5dfa1b9dd45fcfcc32429e78288f03ca706cd9d88edaf37a22c9274eb6683e5a12d9251dbb810c6a0b361fb4db0bfb8cf2247c8fa925a74c67184f57189c5e4faed239a38758fb1f7ff14ea900836d8efdeba40a0400af1904ccb402889c2c5b1c071f6c78ebefaa9865c396628a85e0bd6ae918c805a67185e903ef9c1bb831c95ae76865bbec0c844949633f1c095b8e2451ba0c8cbd53f500b3f7d3362068fd29fbcc66f989df51c178a9eb509b5d8f0bca1f2d73683b549193cd9b5796c3ddebce26452a6ec21ab03539d304ba2186da07dbc8d1932233953a84321fcb9f8af56ac6a9b40785f488ac0c1e7b37f418088391e1a421d16e1509cefa193ed7c2dcbde7f35356db8448d65c638dbff87257b692b8dcd3a51b62b89c68fa6fafb169bc6cf18a0a5ac87b9d95087dffe655bdee022ea4c9866f158ca95fbbac28863d6036db7eec5b650b8e306d99fdbce83b92a7967d5ac00c2a0d2ac2ebb035a65561eb1f1908317dde55bf2ee45a0de7834b8a12fada7e504e77c570d9840a39d8fa8563d676753f4113977b7195a96515f839e56c5d753c7bd2c48a6b405daa2c7ac42261555f79a5ebd1278d73353b45ca30578ed0bec1cd00a4aef63637afd746fc46ed84cfd34f224fdf02b2e02f9a5a02c2661491ab62e46f1acad85924251025d473a05430e3ac7995ca5f56df541229d6d335f1bbf37fad6eb6c10ecab2ffa7a5ff095cf3b8f10598d9a0692c1f504f45cf438cece821e647585dd322f48451a19b53f7c63664bdd61924f8a0e5347e4a06b977c65c896a3022587d8710eab78b169bc83f271e2a5250e8cc601cdca544f8eb8731e957722ff3ea948c6fd9f77fe1fcad397f0faed210bc9849c3bb4c3328d282b327ca892756cc5e33ff72a0c7f37af13a271d7d824a79075a1096982c1df4482b37bd547f485e2b9de53a87c8f4671ee18a626c92a72f51273a3dfc1eb1002dc4d79f2a69ea6b1c7dd682fa0ec314db4cef997267beb735de691f51faa388e46f11248507b44c41c9e8deb583ba570a30bc43c494bb1a25374208728b8eebe7828ff8bc296725f32498a7ffb227e2a5b1c440c856c3fcacfacadfec45aa6ed2e10b2a539d9bd6438c6c61396d6d845eea172caa6f52d5f4efe12ad6ae8b02406aac3e6883a63c12e428805e4823eaa746c8144b8ced1b519b46735db4dd863ea199a6e1710c7c2846722bb29d7c87f8173ca00eb18876d53cf06abb7e300ba956e92af5bff21642707f63c112e47730be34e7ace7ba5d98b0681958d7ef305440e0a295976b943e9ed3e32a843afa7912730c5770cf3d04921b2d633f434f7f5d509bf525d5dd7e9d1d051286abfd62b50d843dbf91131674138bd3892e702d38aa1e392866c550653dea2eb7bc243af6ebe7c68627c88b5188d56139b5dc0a0f979b62497316328d72d6bcac60836062ea31b516ff9f3861e25c391bb693c303f1a36631a2c1d3b3e34ccbca52802a0e7a8940d26e2c0eeb64f371f9624f1527f4146d16ab2a5d0232e99684ea442dbbf7b47539c2fe7c0951362ea173cbfa70d0fb3ad0bffd3430313d071d5fb024541324a7ca14ff51b9c071972970c4838e1c5869b02ca5de3643ffbe5aaeeda54b7dc3a4f2c8bd8edb1d13b62fd82e2c0e4f53192bcf2af57f43aed2c35acb3b05984fac99d4a405b4ef90a1b02d7fcb56bcc951f92ab8b5b27563495a63bbdece0446a0c2c2f72332825f380e26d9460cd8ac9e6e032773a44eab9206a71ceb7fe26e1ea29379b3c8fe67975c2918498315c8389f15194b9919d19b205694115c29a3210aac9db284eb492d87c430ed77be92b34381a5f3c63a158b18595e4b6b5c7d090d29255f432a77311f7a5d4b67d4ddbb0aa3ea15f5c37b67a1f770671d964491bdd6da387e42e9cc6a3192ced55320eaaa728e7b92523d17ebd5ec7343dfd7bbe1a3967aa0a38358bfb984d61196acab2772a584f309b57d2b2ad9b2f9f849daef7f3d06ca61680ae3b5cd42e519d89dae74b153dfa53c61b40b8e20dbef546873e7deb7fb3d9f7e47d0d983bd5a57d41b2794a522e7dbb6791f7b70853a957984805d0b9e468367b432b32fc7df04d0735235adf2793fe389fa8d9ad711d9d4ef5f45d3473324e1277e52de10f59a3c3d692fa28a73a99f7e40468c7470470bc9cfa7d8143a49bb4aa000c9117e04173adff6c327014e7058b26951450152efb18dc7be703ee0ab96f83d72ebcd9699b3bfc9d77c4647536953e77a5df9c5d36e223adbb06639551071ba412ae911942cb32f23ab24fa5788c9c6a78ccea5da78a49934a4835ff36f15eb9112448929218a5d689d92a18d3ed65a9419d5314b1a67478c13f2c5309c69ff77df7bd5f5480f9954286f49140dd7e948841484d7336e2ede88e33e328f9f88c621ae1a01a1ac7beafdf241fd280bfd552b785d435ed6e66fb87dccdb5ea88ad60e04731b42e219bffddf2836defd3e450b43d59a9e844ea3cdab508aa4e6de56a8e114c68524c574939184dd1ae89e06352ee042f346d3eecbe5ea6c2564b4d2ef9233502acd53c45c5f72203e465461bcca6d2731e808a8c0ff2e8abc54314831901573dd5cd834afcfd9c2a0e2825db6439271640cd0d69f8047f93682095ec048b5ddb5a3f480b2e235f3db599601b5b624a0c629838502177eafc0aea065e1142e35b6688d71cdf47fe193c5f4adfd4efc22c7319d368c78b77a0c5d618f2948fe74f1ef633963d7b64067a523e1475c68fa9c11032e1e73b70d5c3d18c3ef7af73ac2d82af43f0c5dfe8c8a4386f4cd82e3216a0f7d74dc60956e36a2918e931cb4bb76ddad0af9e28346a0d0ba60250e7d25f0c03bacb8eb950c5adc20bc7651d3d68355d45fc3a3dcd3d302d1d4a02579f530c2d034e1c4a7b117bc09d154d6c4ea28dbe17f6ea7ece746999b50ee082b5630bfed65a4ad1d29f38dd5891b38c5f869910f32997ea4975dd0b8382a41642f2d79d21e439d76e3e5e2ed183393e87506aedb327e3fc84a92a9796e6a8dbf944deec1758b1b423dee373187969fcd549f664f79b0c79903f2dd7d6135dbc860ae9ed1175cdd293ae2975fdceaa02612c34043065eb35cf867218dad84db7c915e56f889c05fe69f959f71ce1b0255cc361846cdd0659356a11978c7840bca7d2f2acfb156666097e6d0cf0ed76eac4a85d8314b3489ea6346e556ae9c95f9e11f765751d09c4529193d385c760b9084a6896a7c0cfd93f06f28655cfef936d7cfe94f4a601182fdb9412cac3845dc76c88dbdf3880db62871f0b756ca56a08d5ec4b4eaf3542c369ed9deb492112991e578b805bab91d50eed59d9d308e4860a1b75bb231880adc315e6db2b7d6c66185c9ee17648ef7a3a103d162dcf55703b3f6bf213a44bbfc7609f0746e8379905315bb47ef4a2e93c5ba13a7f4856f3a203752ded4ec762c472ab2f3738a3594c3888d3d18f4ee4a99a1027c8fe328dabfddde88890d4e0d39003db8e812a9e9a1fe46fba87acc9c731a1e5c8986daffe1613d1e02014ea4423f510e0a2ae340d1f56bc902283867a237b52592e1164b621306b1bec5ae5ebc36685d37cca46acb7977f26dfb6ec64f7057e6453c9eb27382f423631e58abe3497940a49e394d235a9afe5e33cd4a92337b4c5590c65048a5934ff75135421d6cfb0ae7844eaf952cce7c59bed5a555b9645798d8aa12c6bf21d4f3e5a43f0d811ac459bf72726a88715bc3090fba35710de5903f3f6533c22abd3e7402382fa03bd52846132fa4f4aa7b2339bae1f2dd94af885ec5e95049cdfdf247a4c2a9884bb6b7c7877e7e07983dcc2802df991748c5c381cd2ebe9d281c2098176692cd5e51a2406a31796597d1c71030e3bff1e7c45c190f8cad90bd644538b966f4826068f5f0e78688aeffc7f46edb407a6a8c19fdf0493b0fdfadedddc4be052602b66f246ca71250fd9fbee3eacc67ac4254c496094a3ff6b0f037e6be11b36989d5766b17639482097310a4bf9f5417a5b7ebd071ebc3724e8056092fba423cd8d02555320012b00846060da3859e5384a8a73eedb06ba1f3d6b56e1dcceb0aa4037a0789717fedafb852ca9efc452864748d9bc48e63926dcf341b80debf685350740f43e88aba9bcdd4e9c645cbd97217847ab7ddd740251a5b833564a92b6c18d4b916ddb596abda1097375fea4c36d0a2fed363398b72a5a24f300253300a5080b82b1a19d5aa8b37059ec4bc25ceae2dbbcc105dbcb5a9166eb248e419a5ae8aa9596f69e08f1219a199564bb471c337514a1509a68f47abc45cce5fa95cb1cb36d2fd0740dbc0a68b62a2927c3298673c02c0b5582066d309a5142a64bf73de020967f6d03f260e3239557dd53440ac8795e0c920a439dae2ec2baa29cdd3f0cbb40df65f9daa88afecba0a9b9d6e23afcb1d5d76c7df740943a38a54bed6045ab9cbe6797f19bafa7aee0f0b1d361046d9782366b65a74e9260256b9359a5e30efbfb3ca46abf21527afbf16fa2476c4f8fb45b623662e2331298f61b9a191f47fad87ee3a313576d0706c224a5adac762366694a2fbff29e3ed19f66e3635f7d22dc75d84fdc45184d113d0f636b7991d6eae53f2d611c5f547da9817bf1409d9bef3f9dae387981a77ded9b98a0e3bdd1b2e90493c6ef24a1658a37d5dc282eea82ef8901f38d49ca1481ed7a404843e6213f07b477d9ce09584591a48571f08f33fe4e1ddb50945e984c019dfe99bb36b0b5fe6ef8e0fc14c4b91db1bbf7b1e408e57f19df6d035740b8d6be63e8947635e4965b2d572393103e366f048199415e0cbc50e33dbf540d38f060039424cf26a5542b8a4c9112fafb0d96a522240191ce5f55f25bbe96a98ce2918b0f86fb45da828fe4723de06c1bb48cf7ad3cdb784ee0fec94056a6e23980a7ff3ccfcea8770d438acdae5fce4535b390436e374053506b365291ba4cd3b0cf8ebb159879be1643e98ed2ff4da471519f0e173900ded166cf744d3da4b09cd049166e26beabdeec8d29922de5e9af8a37308c80dba6797468e5710c38986e5fafd34e269411842c734937e8d9840061a000d21d794ba7f1d149a875be4da622b88ddc617457db1a8e697f117a8475fca033ef03dc424046e601bb471a4628471b70c35b48daf8738a0a7e8c71f63dec1112ae99a6c4edc059315a93e9a5ec6b40313ea80483f6cb78e40b814ba1890c4c0311b67a429b27ef32955679900d2549c17f0bd0b80de586d046713b18e60c406f497b92461e8526912d3c33dd198ba107dd7c9d2264a4cb51d5eb9d4bbc33ae4a55f4db0eb7aa7ad15926905a6c5ee2058637e2f1c94701daf8d499c8f13906dec032ff48fe42c40fc95b6f20c4229cbff4a912d783c1325f2522a9ebd4255445b4a856a0dbc2d3d8e2d5d269aac4acbb85ea975f3fb793bd3a2f51c3d54851a4c9e6a201bb29e66d51cf43d21ae02f2fd01b069a54e214bf9fa0f8b4349991539ef53229171c229fb4e5a94495f25e995b5510ec9a35b30c657480a3392036c50aa386b90c834c49112961a67afa8827051ef87a1e78760ac1a52fb7f8f3663ca872479e8e0c09641848502476d8699a3aa1d663ab6892d7bf1fab3bcbadbe11bd8e7325b4368ab1974af9da39fb671939161b06de496ac969bba305c11d7c42d26350261ff2eb2d68b990fdbe7b1c2cf0a91a7bb6c64b6cb9bb145e0fd616ecfd4e92723887a48fd9c407cea7d01340eb5f59501f0ea56939e8eee32e772e252b2def0902063d793fb281b3fcb98bd75d2bd75d9df048c827eb178e77f2cbc27da9983279312a5dac83bf7874b284a3cf2880e5e83aace6692c41db8a99933cef91a421383a383292edaae8a6c0dcc176ac2125981a7920750cbac5377e224971e98862bfada5593d80a77139f3a1a31691d8dd05deb6d3562f7590cd363f4cd873dd8dd0c4be2cdb26b0efdb770f7ec80aed4bcc97062f8df97f326ebed3cf3239f9720508e1e4c4309d7eb57e2912bb01ea0c4384fb58583d0b97af64cb3306f068311fad392f7654342dc07cbb6c3290f84858ff5239e415c7c847d15836b92a3a0358060e3bc1929599edf456009c8328b26a5bb743d0263d437ffaec3ace15df6c6e3366478c51bd991ace234571d44be8193a0e3144c0fdda4df9dc8507918219c13f35edf1b34f96cb0ac6134caec01eb5b85fd9238495dc0854234f4d85fa428a2ecb1e4a0d2699fa31f9417978123dde0496c543008a83bafa7d64746316bf6b296bda4c923ee4421be3e04faf38ab06ea5e9d8d836436f9979eab9ab2836f4e220d02d6ce5d59daa68baed2805197f247f0c8434e6bee4c06a1b5766dcd797f3f2fb59ca80d4905c2842143d6aaaf023904df3bf25809d3470582f17de21ca1271e7541d87eaa0d983c4334c50f2002b5b8322a5875194a8196a6da41a36cab488cbef0f04512c5a6dc8b74296268afa2f0341cce1bfc67cd5fa872e4d2ecf1301e43caf0fa246140fe0948adb6623318929ce30ec1378a342c18467b5874f39bf73d6a3e568759cbfdd0b0ff3cd5284fdba7ee3234094566f646c8da686ac620a40b2907129794c6e74456ee50db41130de879d10850d0b4e2d245219ddfa0acf016a5990e55f17fb545ecf9d3a88ce3b5052681045484a74807033ec6eeda29090894a4a87624df9e2d46c57d93615b636ae8002794faa857db2650af0570599967b17f0fcd94b7379a4cf22b13b93e98401bdeafbb44cf155cfd90a619db5ebfe629e052e3043991985dd0cfc46b6ed8a39ec2b288ac45bdaad6ff5a0e8fb27e1ac536face600b9e0117e22be8f41074805b1aa7ace57e3761ab43dd7e7cc0de2de4c936995afb7186421916919cfa88711073f2850454d4d45f0e8ff7cec387fb0ce1f7a824ae627bc88f48bcbb26f05606c124b80ec8515064689a3eb96e31d3d372f4b5dc2750c857f137828d723361e698f0ddeed6b3cdabe591e86187d7453994e90be122b3533fec94be341620b39d4dc08c63d90b86167de3677132e9c4889764ab2aa0bda1fe679ceddfa62078708e9739cc1acf2fdcc1e820db51bee12e68d4e8a8dba8a3f9878ccdcca9c83c2ce53daee42d4a902e38303cb068bba8579f9739e6316424ca7868eaf1d4fcd2c16ee91b26226cae1d07a704c55f15b76523b84499f3b883412954992c5cb2c22519c8832c2289c9f6ce4a40041997a85d0fb5a049d637982e1df733d2b0ea5bb743f1e23e87ed572e35e5e3bdba27453158b6c0d716834e5e8026839c61e4e7d0c428988f8f2c1bf5245aaf779f10df6b185f57bdbf91f3d0740cff8ef8878177376fbbb55404a42956d661f580ff05e6a582379612945a9b88d4ad3285041548017ad55dc8df26791d7b7e22f497d7a7dc81db13ae3805368d57ecab162d230ce4966eaccb2117e08265e2b0b1c891cb65a0a69f36efe3e8ea78395b4a90781a5cf9980692b61f9bdd7bd8a7ed580b93c8d9e6ac3fb3b1ecbf232e572ddfa5704e76658c431e97a69ed7c84adc7da675ae68d84ec2be0c479b84dce24094826a46fbee27999143a91fb210348334e17e7e3d4888583e15b3228a854654a195ea9953860c63dc7951b05988b1cc3d2b8e5bf1ba83e7e5336a14a25dc57add5e1059a8966ca50ccaeb512eae1b72afdeb36fee28b602b66bd479560085c8dc2b602030661dc22e2df4cd4ffc2b2943cbea1930415f3d5d9e9ee2a657df8476fcbc6649d782e2658e7a28f00267e3d5bacde9bcd3e508049f6d27062cca8adb706939d58eb1a7339ef3bfc6e4dafd12e21fd5c20d7b3ee1140e3a57a4d16246cfbbab1a2c32b5d0f045149f1243bc8b6d274ca9ce2b5b06ad73281553a41a2c13dac14d6a0a7ff9046833a445e49dd4067d5cb1e0a7c253c2ae9a206d21b87b92a8df684f4823fae6786d0064cb931346209a8c15bc7e125b3843939a1f39b17cac576a7e13ed2725f6171550dfc7723cef393f9c94544dd3c88d730ff8df33646c9376a1b5ed6ed65e9554a7b86e5abae8260e0484619eddabe3cf4475a70c24908dfa58b80423432287d2fbcb35eb020bf36fb568a90fc3b72c98967667cbf283d6e64fccb278880927ce39a334a37b4adbd052f8404aad307ba17a900f8f56a25a1565a37f1c6150273c9a49e49adacbf4f38a930ebefe52435722acec2804f5b74e3323990df1925f5e72c4f8e73e3543edd5d638b386ae4fff6647bd0a060692c020caf59ac25eda7d39b63b7d26b4b872dd6b9a3ccb64854d42a3e4a30ae294b6e4511702203ecd2e9e1b51543983dcb5b85cfe25460aa53e1c65a83c95eb9e6772d59942c54f385a4a59e6292f44f938f11f720136ca715c7233a1837e5ed70a8eefb43468255091ec6220aed8c0b2a1bec713ff5c00f0531325c0c1ec854c263a8f08c0849917d8008ae32e6cbcd1efb43ab7c0108518af4bf90718d3089e1120eb7391e4675a34e43e512074a5ed929024065e52863b306d738b9b23b51647b4971ea5d90a7b8d951199d5f331f1abc8d2a8849ab3187cbf89e82738f2f2f35e09abf33f13bc2a233d992cdf457fd02142962f12b67b8ab8aede6434f86a75d76775acbf900e4d427f9d2ed12e68ba596bb15a497bbb6992b40e3b469e34b0a75b1152e4337e6b75319230068e69d5e09de78081939253e2ad7e7069b34c370185bcf3a707585e818d0b24df032dae5ef8a73d620127e3358b8c4342abe281e269a002478e6666c0ae2aa5a4484a44d8edaa7f83594bab3b774ff2a6050d3dfe1d2db1e54fa129aca7d71e17ba1b6575034364375f3c0ffca2a15046947335744da239b9ad82799b1208788e2795d6a8f7b87f45be028708d3b0beb9bd1bd74bc303a9d027a1f96c78bb412a0e8a1da6d4bddacbbe02b7c838d481dec37118c0bb3f7de504d645b09568c3e10f3749df0b2c75a3d353fa9031c77c200c0d3cbaa80c75f59be8876734532d63232909d820756470735df17c6f2e43c713d5e0d0dffae41c8d316f43035c7cca43444060f61213b105eccba1b642b200da689aba125c6d11706cd198635e581eb956ae04c9c9f21a782202422bf858e361fcc1647e32262d6493b11659ac39297ed363b278dc48b22d369629e2d23d93e9c6582a83d4c020f319cbd5f0c91c61bafa0db5aacb22131b1551b28f92fbf3b57e78744ccd060ab9211fbd8c66a76f692046896b6f232dac9efb75b789dbe66e4c9219d3f1882267002b40d6a59f71d3b2e644d63fb582d04e3f917289fade40333f935d1039e9f2be14c304b4df97d5762947363ca03ee95e34a3c113085c70601fd18693337b69bd0e32a0457343aa4469742c4b20a9b5a30c5f745c5b9e0c71265dc5c621e57842fd02fef68403b747580f7567b1dc42a1f0c9d12c29e4cbf4adc74e3f195c152ac6d9f7abbbf1d23bccd237c8aa35f8183ae64b6514ee06bcc82a322dc927b841e848e8adb18f19d94cefbdfcf7a0ea9091a0886e909fc10418c0b74de10db85df88e142598217bcb7604f070afab9899dad37f4fb886ed4a14e09517958d189e5f726f01b394508b8b184111c661429aa1cc08fd038c2b5fd908c33c4a54971a1f259fb3323bb645f0438615a95438bebaab1ae00804c8209dadd4435e0361d952757649f17a9bbf8535e3529d239323cd43e3ea29601960fc777661cf206c0c889b0c6b3e14943a0c06bac6b7fd11102bee2b44be2a4a5e4d631cc6c555d17eb9c712e83d358efeec0ee0a7039119196207731a166e81ed9c2be010cec84ebdcac2ec2a989ffbb70a68b9176f2f96aa6f2f824f4df92bdb2efe522e0f62baa6d215bd2efb0aca702d9142678e67e1c6430d0d0f64d80c63219af1f8a42811a6a6e9f0ca8f38a0f238d6d8a4879896609462bb9887b4cc93e11420c517b2d3f8fe730ed16bf72da09da365a861d8f2140bc4219131de46b63623c57846c5f467036dc34bfa94df6e7cb83626dfec7538e1943e4bfa805f08e07ddfd5aed328d75f3cb8ea4b8d4c963296f2a7ab13ad15412f541c4fda4e48ea1c99552ed31b7e85f3baa82651b654e7ea5cef30e5411e11c27745c0921a5eba6ce9defbe983a0526e459cc1a703511b41824d5a0badf1f6335c1752d92568045c9c67c4017835c7b476216c358c42a9dbeb8e9320e20123d62c008d515629b2b9f9e81dc147e0fe60e9b0ea92980f897a2f125aded646e7b5c532029e9b57016ce064cbe6961104df19c4d70bb80f2fd55e5236d8a2a4785db5349062f992a077278c1e1ac2514498eb9a7213af4e9e4154c3b60f4e79f8cc96d1860fe390b6f59ae273f570dbc0a940622cef71e5d9bec9fbdc13f3c9897bd7fc09be291ef7b0549c2919d647851b7412d98fb29a35c283ba9a5f0d99fabf628a7cd7cb04669a412cef40c013fff6fb55b8b0a69b15ce11e37e0beefe9cf8e7994660bcab99312f8c58d9f0dd9461df9806a79f07a7d0b644399ca079256d3c706491f43f4e276ca9841acbe56ec41fd8ea2d53a9a6bbce1710da9f99673c7f15a709f00435144d63c8d6a78420ba8f7b7fd246cd8804f5b172affb02f5be5b832545673bf4cc9d1e95328e8376e82e734fc81009874839cae30019cd121dce51d3bb1efe23b27f09b73582502e088783a85315f92202d1ef9f15dc8c22d1836338d6666e29c28963ba8b7b67a461fec3c2230ad1a3013f38350bbcbfadbc913ef50ab55850a89920245a4b53ae34626e75c1ab1b7c2dc961d93fc51a26090c30d109623e55e01a75bceda53e36cba9c700c766a475c104364bacd7d94673f6ecd93b81a0d60bb1dfd9f718b0b3cad77458501fa8c8437f8f60ea640f8d21e6fef58669f6386dc951f0e7d76d447f367756edc8e6bbe9e29955cb9b256776520702410bd4d791227f9c53af0492d01f4161fbc1d7d5492820f0399407b38a450328addc240ea1dd24dc5c25fae389feca16ce7ca96f4ad6bc51ccfe89bb34800df17eb05d77e5ef19395a942e83da49c5b130a2a22430ce1f6dd4aa0d26e2bf205cc67f1f8413119bd34e068a2e850c30fadc43ed104fe701300088eb871c42c1773843f45bdebe397d372d83a0cd536a86c37f47c89a646f382dc9512d93d19ef5472d941acbb28b829a5bc33a1767475dba254d3b753c7b90979e28a6d0e44710bc78c7de6295454c2aa7ac25175876cd3e95bd5c33b2011403a7d2e7ed5a1df6d3c7ea6ed31b2cd8311722438d5b9ab5770e15155635b298787036210e31e9985453c6a1f72bbea12b00cf3246c24af38ece542219a7b97c6dbb8eaa87854647804dbe36449c203fc2cc42514a948d6cf872418a235ee661fc104b31f063d957ef089ab5b888aaee44d7c7f4d95a25ba99d630bff29e85959f5fc8715b339d33fdaaf77968bffc5b8296e5dcb25ca65c5e22155c6e1f78c5031af15c43f794fcba43d103eaebb75598888cddd83afa73f94292b876cb8cda5bcf6b740476749108a68d1273e0e3507710ee11b0f9b458b03a2036b5a984f057d1639b970b98e49e628829bbd1ff69913496ddfe39bd3db371483c6576dd82b7582d1d70c7cc0da0fab216ac824fa505d188e58bc2000e55d7223723a55313cefba899fc36c2840682b773d02f4b22adac12407dfbbdb21b9f1e3ba2ff8ba895cfb9d6e0c8caad58dc84eff0463b07dfb61d693dd52e35ba9305219bf0055c6dd201979b4befc8eac28c5a0ce7a0a4d185e79f87792b0bc2e4291d5e95bcbf2fd5f444df5e70cb99a086bc9f6b366b2f76d05ca7cea6bd584fdf7de5bff8cccaad3f07dfef1b5a4e3b51c605e1b7e33cac9da29bb5793b30a372cc7711949d334515fec6f97840c37c403b1c7bb8b716a50bb79c7d79e8a17f70e68e41103c40615c25c3380a9e61a89924524f06d635d527111848e9071a064653ec3ba665e72103cd741d6683ad769e9c747931398fb6bcf13b69186ec81442f7aa6543307b94d9ccd7e6f5a0f4cde4ebd2fbc48fefae45ed27e10acd6724e11cf44abd08e225875e0e7d2b28fd49fee60ffd5c2ca484cee5061f879dfb7df946e80dafa67d9b217d7685c332bbf8ccbc34efde4f6d7f45a8832de340a2afa83e104ea71738db0a5a467bf6b9a64031bfa4e4afb406ce64a32092d75851200d93a45039dcd519cd31d272b106fe330741b8e98d40a19e255f59d2eaeb5c8cf773c021270c8aea669cc20533086ec0e94eefe10e57eb096f26b44f8aa2f55b4ef655aec4861e7bc16283af2a0b5bf89b6d11003dbb23fb8641b75828e86f3770ad2f02e0f11397429bdf8b4bf7000ba893eb93d1ee21128d89835108904e9c19b92c1a470c1fe43015bce854358d729cb129d1c1318a5c733808683e10ddaba9722d5d557304512188f1a0e42c1307e93246fa285a7c454a6c80e6671dcd69cffb18339914b4aaf951c2fcf9ddc4496d0002ed733f7d11592cd42856342a230bc97dec90fb020734bf86ed87a973a3546f44975806a98ef815204c5e5a6a5777a76a8da879c8b528954905be383e7faff41fa491e0a68e45217dbc468a6f03a7e1c3d9436188bc0a6c51b793d178c50929e6cf2ca430305d8f19457f4b20084e914782270d056bd10baaada0eecd18e30849291900db71fe3598bba47ea5493ebe13463ee8bbcae1803d5f41febe4219fa56d6ad1af3b592cc0bd0961adf66976dc51c7677644395dab0b539b58563041c1af744ecd6ff06c791dcefe742dbe47ab093da72a6f7a7046b6a05e3652efa45018f367c6f6af2bb9d3cb69da9eba5bd6db3e9cb782c82ef6864e7d03d173b365e3f9f1aefc18fcce263d0079671296088668ad9f98afbdf78f8bd39fed05e2d15dde760ed53e84922a5ef83ba96083b8035f89a6913539c22753fc0924346eeade7c34ba8268611ed2ca1bc8e5857323eeb49b44569c18bdfe7807d50616fa5507f9db39b91162d836e47ee5d445366a06df1199b28fbec75ba6da3aaccec2625e9f3f5c8d0fdd50f7e99a41d149436cd42bbc1992bf2d5676afb41bebb3ff7c248643acd8fe9e652f99796b4d3f16e1e22bdd8fb6d83022454326d492be8891d1231563c8cc4d1f28523d6d647b0a9eda3a0ea90f3fbbfeb80109701d7c88e6f73de5ebf59787d84bba8c727454a16f70ff231d403603d01dc0b2082e8f31199d84a9ff9c10f1c2df259fb5f8bc4e4ac271ba580bf171f695d5f9bc10ca681cbfe9655ad900c2eda6e5aee465c9b6cc503cdd2e2dfcc2f13b4d8efde470d1bd100255050bef7677ff2534cd71998ec4e9dd8abfc70f7d4b14bd63e6b5cc455b35664023a8352d36f32654b36eb86e2531c3e6eaf5e3257e3ac377b237ef65d76a7aa2c67aac913d5f86a9b3db323eb362212297411443dde7df24bffb341bd6a1bd96f7dfbfe4d5f7bdaffc86aff576331d087785f41221477396fea9f9425d62aa46a275b7baf66baa77953b4d359d078cdd2c6c00e4795827222bc26f2b8b5438ae5476008699b756238eadc931332363f0f2b5a1c289c27c4b5a0c5aea9aa62f5d1056d7c723f5d50b9497667442a12648dadaf73dc91b72eab4366888537191b7a9c871e0c97fa767fd47da8a3df13c8c2450e0a7acae413a959b142415cab21eac4a20fe59bdffdad21d3fbd07ddc02a909f7af2c7edf2f9d17a11d4df020fff78a78a22406ce57d05d2bb21754070c4e0efc80451e3394db586beccbc78ca66c6747eb1f76651d10ac8a8645ac5b3255b101a1478ede6d9505ba6fda2244a77afb69c458af005f7543a11cf23c74793fddf16e469c490339b2583884bec73363e414d551d64eb97fd1901664012120bb84aa101025997f6efd250ce3a1e3876338fc5e650dde9f8886d85132098e58c847b95f3ebbfd22fbc289584185904e9a9aa3091ad7bc84a3275d1b454debdaf40e8647b34124c932feb7e4c972c5a2ec2d2fb924aa5192e07e3a4cca5311e4744f512ec76ff885d148711e8814274f8bb754c82ed4036807128692a269e934caa0e26e7f1c8efb0f3e218fcd1f2652a4b68fd4a418312d75fc7730702ea7dd9eb6ee5eb97e79a3860d931ac241c385e536a95e71a42a010c700e238a57bc84d1f810767082909fa2f8de952cd470992c1a8061a99ae63b93261a492eea39004d80df16aed83df609d3bb0a1cc672b3b9cbac12e738367b0e95114e1e6ab49353d69af0439bac8d3575fa7a0e6e8b23c5010a1bf342e652dcc224d48719b0582fb062a3e0d866269eda27201351b447dfef74aa454baae41a7fd4b642d3929251b66131edf315a0b10a6b89553752215a534c5f32b8a2ffb4ff9a444fec253c2512e95c9c22160ab84ff2e6bfe378d909abfbecfa33ad75ad123ffce9ec0d0f3944b8e6f412bcc);
INSERT INTO `evidence` (`id`, `evidence_number`, `fir_number`, `crime_number`, `type`, `description`, `custodian`, `is_active`, `image_data`) VALUES
(6, 'EV-FIR-KPS-3-6', 'FIR-KPS-3', NULL, 'IMAGE', 'dsgds', '', 1, 0xffd8ffe000104a46494600010100000100010000ffdb008400090607080706090807080a0a090b0d160f0d0c0c0d1b14151016201d2222201d1f1f2428342c242631271f1f2d3d2d3135373a3a3a232b3f443f384334393a37010a0a0a0d0c0d1a0f0f1a37251f253737373737373737373737373737373737373737373737373737373737373737373737373737373737373737373737373737ffc0001108009e00ee03012200021101031101ffc4001c0000020301010101000000000000000000040503060702010008ffc40040100002010204040403050801020603000001020304110005122106314151132261713281911442a1b1f0071523335262c1d17243e116248292b2f13473c2ffc40017010101010100000000000000000000000000010203ffc4001d110101010003000301000000000000000000011102122131415103ffda000c03010002110311003f00c4aed1dd1c1f63d0e3e516e4c2c7a5f0e6a3266906a82a149fe973b9fd7be009b2caba76bc90b15fea517180814d8838315aea0e040bf9db05c3620ae20d7b4fdb3f64f42e00fe1c706aff00d12807f3c2ba78121e27234d80aaff00f9c19fb3195338e11cdf87e46b491ead17fe9946c7e4e06126771551ce72bcca9efac26a9a3bf22be5907b82397ae0957ea7951a8731a0a90045566405ed7f0dae7cdfaed8a664191d55067b96fd9a8eaa19a1622aa6d07c1912c6ec1fe16076b01bdfd8e2d74b22d4b3346c19675f1d08eb7b061ee0eff318af56f15e619667735350e58b3d3d259a7f11ca96bf30b8caac79865695e5eaaaa2710cca0542c1310d220fea16dec39db7b0eb6c419cf19d0e4b5ed97ae5f5550b4e8be249001a21046c0fcb0c06674716529999ab8928648d5d659ce920372d43a9df90e67b7413ec51e7104b559066d4eb055c6b14ac6013062a34ea1e6166b6d637180b3f0de690e66165a72c1651e0c8ae3706c4a1edc830bfa0ed8cf78f5b2d7e2afb650cf14b75686bd52f74963f8811dca6a17eb6f4c5eb86f2a8325828e969d9dc2b22176e66c0f4f6be29fc6fc3f4797e6dfbc291dcc99a57349223db4a791b55bdf738b0ac9736a7a8a7aa06a376de30d7bdfc33a3ebe5188505c38f438b2f1fd3986b2849b1f1210cc395ce94be2b908f39ff0089fcb1401543f897ef8e60fe68f4df12558f329f4c7541034f23aaec74ed7c513a1b018ec1c45245343fcd5d87de1cb1eab5c731809af82292a26a59964825689d7e178dc823e6301eac76adeb6c443faece20aca20d59169aa0e3c578c584abeaa3a93dad7df0ea5cb6a295566942bc12ef1cf136a47f9f43e877c52a15f1a752df0af9cff81fe70f72dceaaf2b958d2386864fe6d3ca2f1c9ee31d273b19bc0e55f48f4c0cf679b95adcef8634ff0064ce949ca7f85556f3d04cfb9f589bef0fed3bfbf45ec1a22566560e0952ac2c45bd31d78d95cacc47133094053b13be0d2b7c414a80b6e76e8713d53f850338e7b01ef850c722ceaa725a8ba032d331bbc3abf15ec7175ccf20cb78cf29199e4f328ac8f6baecc0f661dfff00b18cbe9aa838d12fc9b0e726cd2b723ae5aba09483f7d3eec83b3631ca56b8d0598d0cb2543d257c5e0e6719d3e6d8547bf4d5ebd7df162e11ccaae9a88c19a24b2c29b42dff005001d0dfa62ef5197e5bc6b96c5526158730540da1b7f91ee315eafa368263156a346ebb06517b8edebef8c5f5bcfc6191e62c9cc1fafebf2c19067457e2bdbdaf84271e5c01d3e58c35826b1d1ea9e48ada5cdf6e9e98fa07b38f4c0faafd4e3b4e7b60ab6707e76787b3fa5cc376a6bf875083efc2db37cc6cc3d546347e2fcbb4ca9594456486adfc6a6917e1f14af990fa48bb8fee0718cd2c960031f2f43db1a370171452a53b70e711b2b65936d4f3487ffc76bdc293d06ab156fba476c01bc3f9a4704ab4ef20488b789048e3f94fc8a91d8d8823fce2c1c559765fc41511d4466a68331f08475220751e2c7db96e3b11caff002c57f8ab872a32fadd7a5a4573f12dad51d763c849d6df7ad717df1f65f98bb5304422b29509d31c84a3c27a80766423a8c443bccf234cc3225cae9e345f09a27a78dcf958c676463d88b8bf7b603e12ca732c9f36af35542d43433a2911c8c090e3ef6db6e0fe031ce59c4f9554a31a5cc6736b5c304908f9ec7eb89737e34cbb2fa515152f5355a582a096c541f451b74eb898bab2d566b1651974f9ad46af0a0899902f36f5fd7f9c523f7e1e31972b9a381a0d3039688b6ad2ec4a0dfaf538ee4e243590c350b7a89aa947810e9b9df96dfefb63b6962e19c924cd330647ab949318bed24845942ff6a8dbea7b6185aa37ed0aa8557134d1c66f1d3a88c7a75fcad8afc7b248dfdb6fae3e9a492691e699cbcb2b1791cfde626e4e3ddb4a2823cc6e4fe58d00ebc5a4001e9867c3e8a15d988049f2dfae15555a4a86d276074dfbe0b8dfc245475d80e78d4f96794f0fa58c2213229dfb6174b96c52dda23e1bfa7238fa9ab244fe5b965fe96c1693534ff18f05cf5e871d32573db0966a79e9cde45f2ff52ee31cab023bafe78b03466252586b8c7de1be009680491936d0e4eab01c8f6c62f0c6e72d0f4eda577bdc9b9f5382035f00bc53d3eee352f71896294117b8c66eb4631485581048b7636c59e973a87318d29f3f0ecca2c95d10fe2a7a30ff00a8bf8f638a7c6f82e2939624b67c259ab7cd974b49124e1927a593f975311ba37a7a1f43be15d749a9c460ecbf99c47946715796485a9a406371696091754728ecca763f9e1b8a2a2ce8eac998c356776cbe57beafff00531e7ff13be3af1fe9fac72e3f8554b16b6161d70f68a0151511c44794917185f044f1160c851d4d991858afa11d3161c923dde7b6dc8635cab3161a69a4a6749a99f43a7c36fcbdb169a4aba0cf230333589258c5f9dafed8a6a30d43b63a043ee7976c72b1d256719a7ecdf32a6bb5318a54e81c693f517fc862b35d90d7d183f6aa09a3b7360352fd45c6353a6e20e2a10b46e826907c1aa00c48b6d73b7bf2c0398cbc4d58c82b51a089b9bd2411c840ef6b8bfd473c635b64cf0db972c47f09df1add1f0270e667a5aa3896a8d4b738a68d20607b11bfe781738fd915546a65cb6613a0b9166b9c34666926937e9f960f827509a65f91e76c133f09e794f58b47fbbe669defa574e9bd81279ec761df09831427a7a7eb9628d1f8578e2a728a45cbf32846679415d222620bc6bd949e607453cba118b4c597f0df134be3e419a2c7576178672d1ceb6e86fe62077b30f5c62d0d41437536f4c162a524004a97b72b74c06a0fc139f52553cb4890b922de2154636ed70c0fd571d2709f12ce42544914501f8ce8502dff00a89ffe27140873ccc29c01066f5d18e80ceec07c89b623accdabeac15aacceb67079ab4eda4fcaf6c1322ff5155c33c201fc59d730ccad6fb3d39d67d9db901cb6dbd8e33de21cf2b73dcc0d65738b01a63897e0897b0ff27afd0000944164161db11336af6fcb057d6bf5c59b81b248335ccfc5af644a24b801ce912c96f805fb0dcf5e58ac0f0c58bb958ef62c3736f4f5c6bfc31c51c2ab964396c4c69634d82caa1831ea4fa9c050b39e03adfdeb22e4c61a8a366da4590e98bfb493cedd74eab75c21ccb29cd32662b5b4aeb183b3fc48dec46d8dfe86bf2bcd868a0aca6a8284ae8571a87a69dad88aba814c6c932581b831cabb7e389a3f3cace848d5743dc72c1693ec350122f75e78b5f1b70cd153426a72f8569e52c018c300847a03cbe58a5c54358cdfc0a776ff0088e78dcacd90ce0994822320a9e68e39e0b8a55d360e50dfe190edf26e9f31f3c25922aba727ed74b2a5ad73a48b7adf124154e079195d4743cf1654bc4dc85bda40549e8db7d3a1f95f01cf40ac4b20d2ddc0e78ea9aa430d2db16fb845c1f960c0437c0c17b86dc7d798fc462f959cc273e241b4a36ee30445303d70c248c0d9d74b37226d63ec791c05350ee5a3ba1ea2db625e2d761114a304472faf5be1407788da55b0ee304c52f638c2ae947c41156a2c19f2bc814055ad8c7f192dcb57f58fc716a8a148a9627a69127a57fe5cf11ba3ff00a3e86c719547261b6519e56653297a497c8ffcc89c5e3907f70fd1c5db0c680cd61b1dce3b53a45b0bf2bcca933ab7d8bf81556f35148d727d6363f10f43b8f5c125fcc41d986c41e60e3519134d5d4f2206d6141e84dbdf06472452103c45623ebbfafd310cb414d303e2408e4f36d3a4dbdc6f881786e843078e330b2836def6defd773f3c7174337a68265b4b1a48a79870187e3886932ca68534e5b34f4baac43534a56d7ec371d70ba68a7a662f0d4c938d215caadf42efcc76e7f8e38a69b34aea70b0bb1363e74dae3a6f7d89e805ad7c0159ee6b5f450081e7833376b158e5a6bca00defe4b5f973b628599e574b9ee622a4652b4e34112ad344ca88ddcfb0bed8b7432085d64cee3943963a1122d2877d8b77f5c5960afa29d1624002b9f2aba11e5f9f5b76c0662ffb38caea555726e28856a08f353d6c7e1dc9ec709f34fd9c71565a0c872b7a9887fd5a37122dbdb99c6dcb454c215812187c051a5515432a8ea2d81e1c9a9e99fc5cbde6a26bdcfd96668d4fba03a4fd31747e72a949a9653155a4904abb18e54d2c3dc1df1c092ff78e3f44673256265931af7a4cc620bbad752a31b72dba5f7ed8a2649c2990de4ad932fa9cc119405a68490d0373bb8e7bee36bfb61d8668093dce2392502e0024fa72c6da382b836b5c4547534f14dbff000267f0e553d85ec4fe380ebff6550313e054c91dc6c244d6a7e62d8ba31772ee6ec771c85b6031c1d4a2d7db16ba6e15acad9658a9a98cb3c435491c6c35a6f63b1209b11d2f8575f91d4d248639629a2906fe1cb13231f9100e2ee8511bb46eaf1b32303b329b118b2e4dc6bc459642f4d4d993b4328234d4012853dc6ae4715d96178cd9f6f7db12d204d5773b0e7eb807d97455b99e61178e64ab918f9bc57e42dbb5cec00e78b1499e64192011d3520cd6b179c85cc54e87b2dbccdf9629f3d778142d494ee419b799ba95e8bf3e785c2423b1fc6d8a98bf3f1f57483cd96e4a882f6b53cb71b5b9f8b84d5d5193666cef2e591d24c7fea523b017ff0081dbf2f9e2bd1966602e4db06c575fe951eb817c7b1d04cccde0b99507c37437389f45544c11935311b0b10dfece08565488486537036d26c3165132d4d3d2c8e91bc32c435a3a82b707b6358ceab14d5375b2902e6cc8791f91d8e098fc27dc931b75eabfed7f11e987b2e4f4198d399225303825758df7ff0038ad6614b51935408ea14a447f973738dc7a1e87b8c5d44f3538d20c883431b2b73527b5ff00c600968d96ed16dfda705535569dd5d503f949d996407a1e847a1db052b433f3b42dbee8095bfb74f96084e2568c8591749fcf13a4d83a7a6529e750549b06162a7d01ff001cf0be6a49233786f6ed8cde35ad171cd62a43588dc10791c5a72fe2efe184ce6092b0a0b2544520494fa3120861ebcf14649ecda4ecdeb82524b8e78cb5f4d765cd50dc5344d230e6c58003f1be212b59593e959189e888b60a3aef809e3ad8c2cb1782e80f9c3ae804f2df7be194199d6d25a23411a476bde36e5f8630a654792c60afdadbc661f74df4afb0c3658112c8aa020e83a61247c411a3e89e2950a8d5bdb71dc6194199d34f668e4437e5e6df00778619486dc1e6a770701d464f41358bd24408e5a574dbe98363914f518ed8df616382943e46a9bd2565442dff002d43d36c0b5033da5792a269696a221e6367d04edd43580e5fd587f7ef71f2c2ccfe47fb30a64844ba8f9fcc05874db9df008f3faaa9acf06310843102d2c6cc35162397502c0df9ed869c3b35353d1ac723aacedbb8b753cb7b7a7e188f24cbe3fb48a96493f800d83b5f73d7d70f1e289c00f1a9defb0ebfabe039adfb1c94faaacc0d1e9b6b94022dee701be591c4a3f77544f4607980824f2eff00dbb8c7598e50956c9247533d3b28b155b3238ec55b6fa58e04a5cbabf2e1e052781353b72b12854ec3913f08f437c115f95a6e1de2279e0d124d3d8b3b8d2ae18d89603b1b72fa62c7519bf88ab4d9be452c90117d7032cc83dd0d8fe1826bb8617389229ab66921f0c1056022ed7b1b6a2395fd01c3c872aa58d02682dd096626f82e324e2fe17c8337a37a8e1b9a9e1cc22058d3480c2661fd366b6fcec47b63379b277fb18afa2bc9003a6546f8a26ec71fa8a6cae8990ac94b0c8a7a3a06c0dfbab2e8d4ac7414ca0f30b1817c5d3ad7e4f2fa8d873270c28322ce2b9b4d1657593b7f642d8fd3468682120c5454c8472d312ffac48b305161b2f618bd9b9c1f9e25e09e27a2a37aca8caa4829e31aa4926910691ed7bfe18574ceecde1dd049cc2b1b13e80e375fda4e6694bc1d5809de62b12f4bea201fc2e71856995e22ea15c73d0eba85bfc62f1bac7298329aa239dfecd3854958e943a4a927fa48e57f5c3ba3975d116002a453327b0d8ffbc5790ad7e5ec625d35306fcf7b0ebbf6c1b4599ac54b24b3217a7a9906b51cc1b73fadf1b73b164caea556091652046eeba1bfbff4704514f1cd4738ac40eb34c63489b706c37c55f31a90b49145035c9f32b7cf6ff186353506268d3528748f617e44ee4e0890f0ad38cca09a92575a2670d570937212fe6d27f0f4c37a9a5c86a2493ec4ed45e18d291c8b65948f43edd39f3c29a5e22a6a5f10554e9a985c10791fd00711cb57475ce5a4ae48830f2b052da0f723a8f4fc719c5d0d14f190ccd781b93fde4ffd4398f98b63c942f40b63c990dd5b17d656a8ca69867990d1e7797ac604799650e44d1adbef29f37d0fc8e20ace16c9f35a7693876b2313f820c3453ff02407a5efb1fa0c5ecbd5419e99265bbdbd083cbe785cd4f3a12106a1d39e0daa7adca2ba4a1cce2f02ae336912e2f8ed268dc968a52add7f4717cacfb1b9f84196e63b923af5c4032ba39583494d1974f876b11f4c1ca9891636e8bf3c7176c2a9726a725dbf8a5cee0b9d763f3dff001c0a3872d248cb323ab8f85948d27ba9bdc1fc316208c39db1205b0c0c577f765446ab242645214f96393e26eff862454ad820b8aa7560b7613273f9db7387da71cba1df7eb7c130968b37a99230e69d674ebe1b15d3ef7f6c2dac4cce47926274ea3aca8dcb0ec06dff006c34ce42cf247012a1577737b13b1db7e980f2d426434a8c1a9dee5893a881cbafb1dc6086197d650d1d24318948d56f3b21173ee7060ae842314742a07357070b6a32e0edae3a970cbbaea00db02c790544d56ad75487ab28208f6bfebd31a0fa294d43054b938694b4ba7722eddce3ecb32e4a78512340140f9fccf5c3321631cb11bc469184df1cb48390c09996614d470bcf593c5042a2ed248da557e676c552af8e685aa453508926935105fc3608bea49173f2c40e33de21a4caaae969679478b501980eca0817fa9fcfb621933982db48b63c8838cef3dc9a6ceabe5ae8f3ca7926b81a1d4284e7651f89c20cc32fe27cb8ea28b511ff00546713ad35a9d566600ba383f3c2b39d9f12ccfb632cff00c515f4a74d4c12ab0e775e58f23cfdeb0bf87ab58efb61d6af632fda6f100cc26a5cbe390787016924ff00911603e97fae29f04e00d2c74122c243b0f9ff00bc4d5b4b59515924e02d99afbb639fddf55220f8430e5e618e926462fafa899e97351aeebbe975239a91bfe1896a9441943538df44a0defdf50ff18968e9a62f1c550aa7c260d1c9aecca074f6f4c475347535554c225baeadac6c3e98d6a6219aa4c524674df485b03e8060b85a7cc26f20b01cd99ac7fd61be53c17535881ea2a92361c832eab61dc7c2198518bd2d6d34e0734d1a76c63b55e9106430e5b959099a5378b04c479e64baa13b5f576e870f6ab83b29ac1534195d378156844f4ccee7c399481e4bf4f423b622a0940a3969eb230078da1ecdb15b0dc7a5af86f90d6c74e29e86a010d4933085d86fe195371f98f63e98ced5c8adf03677986419ae6196d652945874da9dc58dcf31f3b5c7fdf0ef30838578f6659e3a99b2ecd615d01e26d2ea7fa581dcdb163af9f25cfe8aa457a982481b4c55013cc2dd49edbfe271967166446491aba2648a75716963fbfea4f30796f8d4f59cc7d9dfeccf3549269a8f33a6cc2400bb23be895adcec1b9e29b554d98d03e8a9a79a3edad363ec791c5b32fe28ce689052677026634bb7f34dd87b377c41c5b2c35b0d2b651535879eb82794b08fb589c686f6806251cb11a95ed8e669d604d4ec3d31cda4cc431b850be80e3d51cf02fda0f87e2caf0c3173d4cdbdbbfa624cb33286b49fddda651cbc661e53fef05821559b655b9c433b4f1380123552a6eccfb83ed6c1db0006b2ce7984d863896258bf8b37c5d139e0b8069b29478f5485d989d449dbf437c174b9352424b47000c76bb1270441ae4de4f28e8060e5b2a817db04c0f1d1462c7c35be094a751f0adb122daf8949d0b726d8d6087cb08d4c45badcf2c24accd5ea1cc740a0ef6323ec07b77c7b5b2c95acc3e084725bfc5ef8f23458edb0f963342da9c9a9ab98499820a9954f91e5dc21f40397cb1537a6aaa7906b7a70d1d8347b286036dc91dafebcb1a1372bf7c56f37a62f553c2022dd838720103cbbfe3f4f5c12bb8323cbeaa9524542be228bd9b50fd72c0155c2484134f51a1adb5c69dfdc61de485562f001625001bfa6ded7f9751860c98232dce7832bc862aeb303fde0ff00f2b629b2e415b974f2b4b4ed1a902cd6d8ef8fd006307981843c41471948eea3cc4dfd7963529f0a4d17054151454734b984b1c951178c54460dc682f6517bdc72df9dee2f6da46e1bca69b2f663f6ea99e3a4ab90cb1a583bc72e916527636e9db738739ae4b9bd452d35253569f05156445d3e18036541e27de2355876b9c2d8b81f3903cf52b0ca2421559cb03b9049dfa953ef85b8d71e3a579970e5051453935ad30588d50785412d110ed1dae6d765504df95f1c51f0ec22b54c55150620fa0b155d98916e5bdb7e83a7418e65e1be20cfeaaa6a10a48ec5564fe26816d02c0016160a40b003db13e57c199ec12ffe67482ccaa235a9ddae5ae76dadfc33cfd317759b32ac745431434ca6699958d998e9bdef61b01723e2e47b1c43356ac30a1859a5129b07b69f35ae40077ff1d8e15f105166f0e5d339a8022a62acfe1cc352122cbaadb92412073d81c25cb2a9846866766d00aa0637d23114ea9a44a892684922424916e679f2faf2c49ade3649d8af9934923a35c036f96f800cbf697d51bf8752a7f84fd1cf407fc6099e51a6e57424e37bfdc6ebf8ed8819a54aaacc80e9d67529f90385d5514798c4d04c4a9d40abf507fd617cb54cd4eb2163ae321197d46d82b2e76a881de1b974fbbc8b8ee3d70821a884ad9258a27005accbbfc8f6c411e4f97d45c7853c2c37fe17987ebe583e59a1a91e1923c55efb5be58f72f46591b55c002dbaea1f9df1a4b1a0cb98410a9bca033f7e985b535f14c1c49206b8b01dc62a34f475999b8a492a8ccd2464dc3ea118bed7ec7165a7cbb2fe1c447a9a9334ec2c158dee7b018ce24f5de5dc3f954a1a7ac83544a764791ca7fed26d87d1ca5dd129d053d30d8796d7181289fc7bd555af87126e11ba63b4affdef230a5256994d8bf2d7ede988e921e53cb1c67452a863d5db7c190c7a9f5487511d5b0ad268326cb9e7ac956386152ccec790ff0038cef8a7f6a551514ef4dc334af1eab03553adbff6af7f53816e344e22e2ac938796f99d7450bf48ef773ec06f8ab507ed5b28cc6b3c0a78aa3517d281be271e8bcf19e643c1d9cf14541ac78d92377267aca96b973d6c799fcb1b1f07707651c33086a7855aa5879ea5d46a6f41d87a0c121ed0d44d32092488c60ef66e7f4e989ab27ba18c36edb1f418459bf14d252ced042e19d1ad290768cf6f7dfe58f9334a6dbc599518f3d771be1a0fd36c7c576be0735f006d0d2206b816d6399e5ed8ebed510b6b6001e5b8c344ab61cf7c2acf60d7240f722e3495e47637ff0078642a206bda45bf6bef81b37412d012855bc260dcfa1d8f2f4271285197544f066ab1c9a3c26174b9bb6fcfe5cbe98b285fea16b8be28f98b08a5a59d4dd2360a4dc051db51fadbf56ba453218949203117373823b6416c2dcd28dea96354b00a4dee70d15d6ff1adbb837c79269617b8f9628ac4d924f247e199174fa31d8e1054504d1d5b5255dcfde275936f5ef8bf3020e937bf4f5c29cef2efb7c41a16d12a0efb30ea08077ef8a7c29d98f09d41858c75aeb10f3e93335ae073f7b0e7cf14c91218c93f6c72c0d820723f5be351ca6a4a17a3a8d046a3e135f502bd07e5b7ae2a1c59c3fe04d254c29aa191ae5546ea704b6dbeab501124a51a491750b125ae3d2fbf4ff3827f77d5c6a5a31e2a0dcb26f859e1d3c6f757d2c0d883e5fcb160c96be38f45aae456bfc0fe71f2ea3dc5b12dad785a92f9c231df91079a9e9820667e3d3cc8c0f8b4c039079b25f4b7d2fbfa1c3ecf228736a5d30ca9e3dac23985839f46e631410d5597d715ac85d5d091e71b3291664f5041384ba61df881d0b2b5d664b8bf52bfe7a1f7182386e46a990411c811f5d8331b589e40fa1c56da5932f2d1c475c4cc1e3ebb8e4def6d8fff00582f23904f9cc71aea11d5a9522df09f5f620fe186d5c5bb30a1953320f3144a959344ab63e6523e21dc6df4c5a32dc83ed7471b4752a9a86a26f7be1266396d55751c75750e29a1a7859aa2596420b460d8016bf98afeb7c0d93f1922c7a1b608a156ddb125a9617d150d5d7660465f24d0549d9d90dbebe98bbe59c314f92c7f6fcc6b5ea2a74ff1659db61edd8602a0cb336cbaa1e6a4aaa701f6d2f15cfd6f80f3bfb757694cd2526007745f2a9f53df1aaccb8e6bb36ffc455429a83c44a006cd2a6c24f41e98b6d30a7e1dc99aa2a0e9445d93ab1e8a3d4e02c8e1a48a9e3868e2fe18eb6daf80f89165cc6b61895fc48d46e47253df11bdc9aadf11e6798713395aa062a45dd69d0deddae7a9fcb1170670f2675c40942d2134f1278b282dbed6b01e973872f939881466b965f883007d8626fd9fe4f5997e715158b305468ca382b7bdc83fe31ad8e7f6d17306a5ca28516201563b208d46c476184d26633cd01088f1162542b3589b8dac45f0cc9b9b9373df1c6c0f21be335b23f004f2c5e6114ad1032a10244ecc01dad7b73b75c12d4d122969523d0ea06c6fe61b03f89c34b21b5d576f4c7c238daf745e9d313001551532408d4d1adc0bc84ff49b9b5fae06d74aab3b2800a69661a85af6b8171b8386f252d3c8ba5e2422f7b5b1c49414922046810a8e42d860865a58fc5652e8ca87ca08b9dc75e77be04aa819898e29195a55f35f6db7b58ffbef86bf668480347216e7d3b63d4a3813cda0ec08b9726c313284b5741e2c7a6611b2960b22dad753c8817dcdee37e7be3a8e99563a7a39276f1a24d8ab13a96e6db5fdbebdb12e631159e7a60c74c82ebfd5f0edd3be14d0d60a3a9f0eaa3d4c10052e01256fccfbef6e4300c9567890c8d33c40a6e36b83d79123bffbdb1cac998c4b1c5f6a9351934abf86183037f5f5e9cc8b61d0cbe8ca91e04455c79ac39e251434d7b88941bdf6ef8b94570cf5b3a3a7dbe1694310b2226902c795bbf3dbf1c0f1d5d6b791a5d32024df4db6e9ed8b31cae8b516fb3a0249248dae4f3c74f97d2b021a106f82629429dea0f8b2346b26b32878b7537e5dfa60892bcd440eb3c2aa58e9dae57e7b58fd7164fdc948ae64895e36ee18fbfd315bcfa89e9a74898f90b6bd3a7e216e961cc7f9f5c54c53b39e1866669a385a55637ffcbdb503ff00136bfd462b32f0e4d50ec28e48662a7cd14978e443eaac01ff001eb8d5e832aa2aca712412d445bd88498f94f6defb622ace1f98a7f02bea6471f089618e41f3f283f423136b51951cb737a51a0c1506c3700eab8f97318f245cc6aa0f02a7c474bfc321b15f9daf8d1d32dce3ecedfbc682196c2c0c04ab1f5b127f3f98c209e9668aa98d3d5cd18b5fc2ab8ae07a6a20edfabe1aa5192f0bc95ae893333a0e4a8bfef1a1e45c159764b353d754d3b19755e362459798fd7be27e1da0ccbc212d5cb168e5a62882dbb75c5be6812ae8e382a031086ebbef7f9633ed159e3e9690e552c2b05e1994ac88a06f71cc5bafa6300aa79f2eaa92172e6c6de6e78fd1f9970ed2d6533c5aa48c9e5206be93ed8ca73fe1b8a82a7ecf9c52c808f827818e9907b1e471d2462dcad0a9ea04ca3c2495feef9509bf5ffbe09120b79e0988b7585bd3d3f57c28a067822bc6f60f626eb7e5cb0c83d53bb9354dad47c5a77e56ef8225a9af8e9a1fe1c2fa803b78476e5fec7d4615c5268594cb1ba86620ab1b58f6b6246a99e7aa11c92dfc3bd8e81b13bed88a672c5d8bb36b3a9c9037be255479ac891500a7d4a64737e6755f0ff87a0fb35026af8dc6a38ac242926630c7a40691b67ea00e98b8c63400a390db013ebbed8f4f61cc6380df0db6c4abe982be3aae3618916c08c424d9b6e58900b0bf7c07637271e91b63853bf6c7afb8db6380eac6dcf1edc7438e353586fed8f40f31c142e6378e685f482251e13fc8edf99c20cea9e38e99e508c1ed70efcc6e4917f7dfd6f8b2568069c38d991c107df15ccd8ae9581635f87c43d3a5ff003c67ecab0e4b51e3e5f1936d4834b7ebdb0783b0c563841a6855a395c3a30257e47fef8b37416eb8d2477f97f9c7b8e7e117c7a3960af873c455700a88c0bd9c1f237638931ea8d5f2df0157024a0cc4d553a6a476b5442a9b03bee3df7fa62c94cf0d44293441487170c0603cc534e99d1992c6cc17adfafbedfab601cbaa4524c546af0a72ada4742c4efe86e37faf3e713e0e9e923246a1e51be9e98e24a0a798ef100ddffedcb056e0007b0fcb1edb1541083c276d03f84e2ccbdedd46044cc3ec754b4f56c4c327f2663f73bab1f4efdb9f7c37bd81385199d1092859948240f36aea7a62066d7e7802b69e29d42cd0472a83701d4100fcf0af2dcca5a1ac8f2d986b898f87190c4e823a7fc76c3877048247cb1a65ffd9);

--
-- Triggers `evidence`
--
DELIMITER $$
CREATE TRIGGER `evidence_after_insert` AFTER INSERT ON `evidence` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'evidence', NEW.evidence_number,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Evidence Number', 20, ' '), ': ', IFNULL(NEW.evidence_number, 'N/A'), '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Crime Number', 20, ' '), ': ', IFNULL(NEW.crime_number, 'N/A'), '\n',
            RPAD('Type', 20, ' '), ': ', IFNULL(NEW.type, 'N/A'), '\n',
            RPAD('Custodian', 20, ' '), ': ', IFNULL(NEW.custodian, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `evidence_after_update` AFTER UPDATE ON `evidence` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'evidence', NEW.evidence_number,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('Evidence Number', 20, ' '), ': ', IFNULL(OLD.evidence_number, 'N/A'), '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('Crime Number', 20, ' '), ': ', IFNULL(OLD.crime_number, 'N/A'), '\n',
            RPAD('Type', 20, ' '), ': ', IFNULL(OLD.type, 'N/A'), '\n',
            RPAD('Custodian', 20, ' '), ': ', IFNULL(OLD.custodian, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Evidence Number', 20, ' '), ': ', IFNULL(NEW.evidence_number, 'N/A'), '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Crime Number', 20, ' '), ': ', IFNULL(NEW.crime_number, 'N/A'), '\n',
            RPAD('Type', 20, ' '), ': ', IFNULL(NEW.type, 'N/A'), '\n',
            RPAD('Custodian', 20, ' '), ': ', IFNULL(NEW.custodian, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `fir`
--

CREATE TABLE `fir` (
  `id` int(11) NOT NULL,
  `fir_number` varchar(50) DEFAULT NULL,
  `complainant_name` varchar(100) DEFAULT NULL,
  `complainant_contact` varchar(20) DEFAULT NULL,
  `incident_location` varchar(255) DEFAULT NULL,
  `incident_date` datetime DEFAULT NULL,
  `incident_description` text DEFAULT NULL,
  `crime_category` varchar(50) DEFAULT NULL,
  `status` varchar(30) DEFAULT 'FILED',
  `assigned_officer_id` int(11) DEFAULT NULL,
  `station_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp(),
  `station_name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fir`
--

INSERT INTO `fir` (`id`, `fir_number`, `complainant_name`, `complainant_contact`, `incident_location`, `incident_date`, `incident_description`, `crime_category`, `status`, `assigned_officer_id`, `station_id`, `is_active`, `created_at`, `station_name`) VALUES
(1, 'FIR-CA1-1', 'mohan', '9874563214', 'patan', '2002-02-02 05:05:00', 'drunk and drive', 'Fraud', 'ASSIGNED', 2, 1, 1, '2026-08-04 23:38:05', 'CENTRAL AHMEDABAD 1'),
(2, 'FIR-KPS-2', 'grisha', '9875486321', 'laldarvaja', '2005-05-02 06:03:00', 'hit and run', 'Assault', 'CLOSED', 4, 3, 0, '2026-08-05 09:05:25', 'kheda police station'),
(3, 'FIR-KPS-3', 'trial', '9874563210', 'kheda', '2000-05-02 05:06:00', 'jbji', 'Theft', 'CLOSED', 5, 3, 0, '2026-08-05 10:23:53', 'kheda police station'),
(4, 'FIR-KPS-4', 'yuna', '9999999999', 'delhi', '2026-08-07 07:50:00', 'trial', 'Theft', 'ASSIGNED', 5, 3, 1, '2026-08-07 05:58:42', 'kheda police station'),
(5, 'FIR-KPS-5', 'raj kapoor', '9874568974', 'anand', '2026-04-02 05:55:00', 'hit and run', 'hit and run', 'FILED', NULL, 3, 1, '2026-08-08 10:10:41', 'kheda police station');

--
-- Triggers `fir`
--
DELIMITER $$
CREATE TRIGGER `fir_after_insert` AFTER INSERT ON `fir` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'fir', NEW.fir_number,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Complainant', 20, ' '), ': ', IFNULL(NEW.complainant_name, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(NEW.status, 'N/A'), '\n',
            RPAD('Crime Category', 20, ' '), ': ', IFNULL(NEW.crime_category, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Assigned Officer', 20, ' '), ': ', IFNULL(NEW.assigned_officer_id, 'None'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `fir_after_update` AFTER UPDATE ON `fir` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'fir', NEW.fir_number,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('Complainant', 20, ' '), ': ', IFNULL(OLD.complainant_name, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(OLD.status, 'N/A'), '\n',
            RPAD('Crime Category', 20, ' '), ': ', IFNULL(OLD.crime_category, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(OLD.station_id, 'N/A'), '\n',
            RPAD('Assigned Officer', 20, ' '), ': ', IFNULL(OLD.assigned_officer_id, 'None'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Complainant', 20, ' '), ': ', IFNULL(NEW.complainant_name, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(NEW.status, 'N/A'), '\n',
            RPAD('Crime Category', 20, ' '), ': ', IFNULL(NEW.crime_category, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Assigned Officer', 20, ' '), ': ', IFNULL(NEW.assigned_officer_id, 'None'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `fir_before_insert` BEFORE INSERT ON `fir` FOR EACH ROW BEGIN
    DECLARE name VARCHAR(100);
    SELECT station_name INTO name FROM police_stations WHERE id = NEW.station_id;
    SET NEW.station_name = IFNULL(name, 'Unknown Station');
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `fir_before_update` BEFORE UPDATE ON `fir` FOR EACH ROW BEGIN
    DECLARE name VARCHAR(100);
    SELECT station_name INTO name FROM police_stations WHERE id = NEW.station_id;
    SET NEW.station_name = IFNULL(name, 'Unknown Station');
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `sync_investigation_with_fir` AFTER UPDATE ON `fir` FOR EACH ROW BEGIN
    -- Case 1: FIR is deactivated OR status becomes CLOSED
    IF (OLD.is_active = 1 AND NEW.is_active = 0) OR (NEW.status = 'CLOSED' AND OLD.status != 'CLOSED') THEN
        UPDATE investigations
        SET status = 'CLOSED', is_active = 0
        WHERE fir_number = NEW.fir_number;
    END IF;

    -- Case 2: FIR is reactivated and status is not CLOSED
    IF OLD.is_active = 0 AND NEW.is_active = 1 AND NEW.status != 'CLOSED' THEN
        UPDATE investigations
        SET status = 'OPEN', is_active = 1
        WHERE fir_number = NEW.fir_number;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `fir_crime_categories`
--

CREATE TABLE `fir_crime_categories` (
  `id` int(11) NOT NULL,
  `fir_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fir_crime_categories`
--

INSERT INTO `fir_crime_categories` (`id`, `fir_id`, `category_id`) VALUES
(1, 1, 3),
(2, 2, 2),
(3, 3, 1),
(4, 4, 1);

-- --------------------------------------------------------

--
-- Table structure for table `investigations`
--

CREATE TABLE `investigations` (
  `id` int(11) NOT NULL,
  `fir_number` varchar(50) DEFAULT NULL,
  `officer_id` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `status` varchar(30) DEFAULT 'OPEN',
  `start_date` datetime DEFAULT current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `investigations`
--

INSERT INTO `investigations` (`id`, `fir_number`, `officer_id`, `notes`, `status`, `start_date`, `is_active`) VALUES
(1, 'FIR-CA1-1', 2, '\nin city', 'OPEN', '2026-08-05 00:33:07', 1),
(2, 'FIR-KPS-2', 4, '', 'CLOSED', '2026-08-05 09:16:30', 0),
(3, 'FIR-KPS-3', 5, 'fgh\nvnvg', 'CLOSED', '2026-08-05 11:32:31', 0),
(4, 'FIR-KPS-4', 5, 'Investigation reopened by officer ID 5 on 2026-08-07T18:53:55.950295500\nhit and run with car number ......\ntrial @ 123', 'OPEN', '2026-08-07 18:53:55', 1);

--
-- Triggers `investigations`
--
DELIMITER $$
CREATE TRIGGER `investigations_after_insert` AFTER INSERT ON `investigations` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'investigations', NEW.fir_number,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Officer ID', 20, ' '), ': ', IFNULL(NEW.officer_id, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(NEW.status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `investigations_after_update` AFTER UPDATE ON `investigations` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'investigations', NEW.fir_number,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('Officer ID', 20, ' '), ': ', IFNULL(OLD.officer_id, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(OLD.status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('Officer ID', 20, ' '), ': ', IFNULL(NEW.officer_id, 'N/A'), '\n',
            RPAD('Status', 20, ' '), ': ', IFNULL(NEW.status, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `login_history`
--

CREATE TABLE `login_history` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `login_time` datetime DEFAULT current_timestamp(),
  `ip_address` varchar(50) DEFAULT NULL,
  `success` tinyint(1) DEFAULT NULL,
  `failure_reason` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `login_history`
--

INSERT INTO `login_history` (`id`, `username`, `login_time`, `ip_address`, `success`, `failure_reason`) VALUES
(1, 'admin', '2026-08-04 08:38:08', NULL, 1, NULL),
(2, 'admin', '2026-08-04 08:51:03', NULL, 1, NULL),
(3, 'admin', '2026-08-04 22:14:03', NULL, 1, NULL),
(4, 'admin', '2026-08-04 22:16:28', NULL, 1, NULL),
(5, 'admin', '2026-08-04 22:29:06', NULL, 1, NULL),
(6, 'admin', '2026-08-04 22:55:36', NULL, 1, NULL),
(7, 'admin', '2026-08-04 23:03:22', NULL, 1, NULL),
(8, 'admin', '2026-08-04 23:04:41', NULL, 1, NULL),
(9, 'Super', '2026-08-04 23:10:19', NULL, 1, NULL),
(10, 'Super', '2026-08-04 23:11:17', NULL, 1, NULL),
(11, 'Super', '2026-08-04 23:15:41', NULL, 1, NULL),
(12, 'Super', '2026-08-04 23:19:24', NULL, 1, NULL),
(13, 'staff_arham', '2026-08-04 23:33:34', NULL, 1, NULL),
(14, 'staff_arham', '2026-08-04 23:36:56', NULL, 0, 'Invalid credentials'),
(15, 'staff_arham', '2026-08-04 23:37:08', NULL, 1, NULL),
(16, 'staff_arham', '2026-08-04 23:56:38', NULL, 1, NULL),
(17, 'admin', '2026-08-05 00:31:19', NULL, 1, NULL),
(18, 'officer_rahul', '2026-08-05 00:31:56', NULL, 0, 'Invalid credentials'),
(19, 'officer_rahul', '2026-08-05 00:32:09', NULL, 1, NULL),
(20, 'officer_rahul', '2026-08-05 00:36:25', NULL, 1, NULL),
(21, 'officer_rahul', '2026-08-05 00:37:25', NULL, 1, NULL),
(22, 'officer_rahul', '2026-08-05 00:43:05', NULL, 1, NULL),
(23, 'officer_rahul', '2026-08-05 00:47:11', NULL, 1, NULL),
(24, 'officer', '2026-08-05 01:00:32', NULL, 0, 'Invalid credentials'),
(25, 'officer_rahul', '2026-08-05 01:00:48', NULL, 1, NULL),
(26, 'officer_rahul', '2026-08-05 01:40:14', NULL, 1, NULL),
(27, 'officer_rahul', '2026-08-05 01:41:07', NULL, 1, NULL),
(28, 'admin', '2026-08-05 03:00:21', NULL, 1, NULL),
(29, 'admin', '2026-08-05 03:02:56', NULL, 1, NULL),
(30, 'Super', '2026-08-05 03:03:47', NULL, 1, NULL),
(31, 'admin_2', '2026-08-05 03:05:02', NULL, 1, NULL),
(32, 'admin', '2026-08-05 03:12:01', NULL, 1, NULL),
(33, 'officer_rahul', '2026-08-05 03:12:59', NULL, 1, NULL),
(34, 'officer_john', '2026-08-05 08:43:58', NULL, 1, NULL),
(35, 'officer_john', '2026-08-05 08:50:14', NULL, 1, NULL),
(36, 'officer_john', '2026-08-05 08:52:12', NULL, 1, NULL),
(37, 'officer_john', '2026-08-05 08:58:31', NULL, 1, NULL),
(38, 'officer_johnm', '2026-08-05 08:59:50', NULL, 0, 'Invalid credentials'),
(39, 'officer_john', '2026-08-05 08:59:58', NULL, 1, NULL),
(40, 'officer_john', '2026-08-05 09:00:54', NULL, 1, NULL),
(41, 'officer_john', '2026-08-05 09:02:28', NULL, 1, NULL),
(42, 'officer_john', '2026-08-05 09:03:06', NULL, 1, NULL),
(43, 'staff_bob', '2026-08-05 09:03:54', NULL, 1, NULL),
(44, 'staff_bob', '2026-08-05 09:14:06', NULL, 0, 'Invalid credentials'),
(45, 'staff_bob', '2026-08-05 09:14:14', NULL, 1, NULL),
(46, 'officer_john', '2026-08-05 09:15:16', NULL, 1, NULL),
(47, 'admin_2', '2026-08-05 09:15:41', NULL, 1, NULL),
(48, 'officer_john', '2026-08-05 09:16:16', NULL, 1, NULL),
(49, 'officer_john', '2026-08-05 09:18:44', NULL, 1, NULL),
(50, 'officer_john', '2026-08-05 09:24:50', NULL, 1, NULL),
(51, 'officer_john', '2026-08-05 09:27:00', NULL, 1, NULL),
(52, 'Super', '2026-08-05 09:28:39', NULL, 1, NULL),
(53, 'officer_john', '2026-08-05 09:35:26', NULL, 1, NULL),
(54, 'officer_john', '2026-08-05 09:36:48', NULL, 1, NULL),
(55, 'officer_john', '2026-08-05 09:40:16', NULL, 1, NULL),
(56, 'Super', '2026-08-05 10:17:04', NULL, 1, NULL),
(57, 'officer_john', '2026-08-05 10:20:04', NULL, 1, NULL),
(58, 'staff_bob', '2026-08-05 10:22:12', NULL, 1, NULL),
(59, 'admin', '2026-08-05 11:22:01', NULL, 1, NULL),
(60, 'officer', '2026-08-05 11:29:08', NULL, 0, 'Invalid credentials'),
(61, 'officer_jane', '2026-08-05 11:29:23', NULL, 0, 'Invalid credentials'),
(62, 'officer_smith', '2026-08-05 11:29:52', NULL, 1, NULL),
(63, 'officer_smith', '2026-08-05 11:36:34', NULL, 1, NULL),
(64, 'staff_bob', '2026-08-05 11:40:36', NULL, 1, NULL),
(65, 'officer_bob', '2026-08-07 05:56:34', NULL, 0, 'Invalid credentials'),
(66, 'staff_bob', '2026-08-07 05:56:44', NULL, 1, NULL),
(67, 'admin', '2026-08-07 05:58:52', NULL, 1, NULL),
(68, 'admin', '2026-08-07 12:08:33', NULL, 1, NULL),
(69, 'admin', '2026-08-07 12:14:21', NULL, 1, NULL),
(70, 'admin', '2026-08-07 15:26:45', NULL, 1, NULL),
(71, 'Super', '2026-08-07 16:41:01', NULL, 1, NULL),
(72, 'admin_2', '2026-08-07 16:42:40', NULL, 1, NULL),
(73, 'admin_2', '2026-08-07 16:45:12', NULL, 1, NULL),
(74, 'admin_2', '2026-08-07 16:59:36', NULL, 1, NULL),
(75, 'admin_2', '2026-08-07 17:04:43', NULL, 1, NULL),
(76, 'admin_2', '2026-08-07 17:10:40', NULL, 1, NULL),
(77, 'officer_smith', '2026-08-07 17:23:41', NULL, 1, NULL),
(78, 'admin_2', '2026-08-07 17:26:29', NULL, 1, NULL),
(79, 'officer_smith', '2026-08-07 17:45:03', NULL, 1, NULL),
(80, 'admin_2', '2026-08-07 17:47:35', NULL, 1, NULL),
(81, 'Super', '2026-08-07 18:25:21', NULL, 1, NULL),
(82, 'Super', '2026-08-07 18:27:55', NULL, 1, NULL),
(83, 'Super', '2026-08-07 18:32:03', NULL, 1, NULL),
(84, 'admin_2', '2026-08-07 18:41:12', NULL, 1, NULL),
(85, 'officer_smith', '2026-08-07 18:42:15', NULL, 1, NULL),
(86, 'officer_smith', '2026-08-07 18:50:49', NULL, 1, NULL),
(87, 'officer', '2026-08-07 18:52:14', NULL, 0, 'Invalid credentials'),
(88, 'officer_smith', '2026-08-07 18:52:25', NULL, 1, NULL),
(89, 'officer_smith', '2026-08-07 18:53:52', NULL, 1, NULL),
(90, 'admin', '2026-08-07 21:16:17', NULL, 1, NULL),
(91, 'admin', '2026-08-07 21:19:28', NULL, 1, NULL),
(92, 'staff_bob', '2026-08-07 21:20:10', NULL, 1, NULL),
(93, 'staff_bob', '2026-08-07 21:21:04', NULL, 1, NULL),
(94, 'staff_bob', '2026-08-07 21:23:37', NULL, 1, NULL),
(95, 'staff_bob', '2026-08-07 21:24:52', NULL, 1, NULL),
(96, 'staff_bob', '2026-08-07 21:35:14', NULL, 1, NULL),
(97, 'staff_bob', '2026-08-07 21:38:43', NULL, 1, NULL),
(98, 'admin', '2026-08-07 21:54:27', NULL, 0, 'Invalid credentials'),
(99, 'admin', '2026-08-07 21:54:34', NULL, 1, NULL),
(100, 'Super', '2026-08-07 21:56:12', NULL, 1, NULL),
(101, 'adminb', '2026-08-07 21:56:37', NULL, 0, 'Invalid credentials'),
(102, 'admin', '2026-08-07 21:56:43', NULL, 1, NULL),
(103, 'admin', '2026-08-07 21:58:58', NULL, 1, NULL),
(104, 'staff_arham', '2026-08-07 21:59:24', NULL, 0, 'Invalid credentials'),
(105, 'staff_arham', '2026-08-07 21:59:33', NULL, 0, 'Invalid credentials'),
(106, 'staff_arham', '2026-08-07 21:59:51', NULL, 1, NULL),
(107, 'staff_bob', '2026-08-07 22:17:29', NULL, 1, NULL),
(108, 'officer_smith', '2026-08-07 22:17:51', NULL, 1, NULL),
(109, 'officer_smith', '2026-08-07 23:23:06', NULL, 1, NULL),
(110, 'staff_bob', '2026-08-08 08:38:04', NULL, 0, 'Invalid credentials'),
(111, '', '2026-08-08 08:42:18', NULL, 0, 'Invalid credentials'),
(112, 'officer_smith', '2026-08-08 08:44:18', NULL, 1, NULL),
(113, 'Super', '2026-08-08 08:49:24', NULL, 1, NULL),
(114, 'staff_bob', '2026-08-08 09:50:09', NULL, 1, NULL),
(115, 'staff_bob', '2026-08-08 09:52:43', NULL, 1, NULL),
(116, 'staff_bob', '2026-08-08 10:17:27', NULL, 1, NULL),
(117, 'staff_bob', '2026-08-08 10:19:55', NULL, 1, NULL),
(118, 'admin', '2026-08-08 10:26:15', NULL, 1, NULL),
(119, 'officer_smith', '2026-08-08 10:39:44', NULL, 0, 'Invalid credentials'),
(120, 'officer_smith', '2026-08-08 10:40:01', NULL, 1, NULL),
(121, 'officer_bob', '2026-08-08 10:46:55', NULL, 0, 'Invalid credentials'),
(122, 'officer_smith', '2026-08-08 10:47:12', NULL, 1, NULL),
(123, 'officer_smith', '2026-08-08 10:48:46', NULL, 1, NULL),
(124, 'officer_smith', '2026-08-08 11:07:18', NULL, 1, NULL),
(125, 'officer_smith', '2026-08-08 11:11:19', NULL, 1, NULL),
(126, 'Super', '2026-08-08 11:50:26', NULL, 1, NULL),
(127, 'officer_smith', '2026-08-08 12:00:33', NULL, 1, NULL),
(128, 'admin', '2026-08-08 12:02:12', NULL, 1, NULL),
(129, 'inspector_raj', '2026-08-08 12:05:11', NULL, 0, 'Invalid credentials'),
(130, 'inspector_raj', '2026-08-08 12:05:30', NULL, 0, 'Invalid credentials'),
(131, 'inspector_raj', '2026-08-08 12:05:55', NULL, 0, 'Invalid credentials'),
(132, 'admin', '2026-08-08 12:06:47', NULL, 1, NULL),
(133, 'inspector_raj', '2026-08-08 12:08:21', NULL, 1, NULL),
(134, 'sdf_re_23@', '2026-08-08 12:53:54', NULL, 0, 'Invalid credentials'),
(135, 'admin', '2026-08-08 12:55:31', NULL, 1, NULL),
(136, 'admin', '2026-08-08 12:58:57', NULL, 1, NULL),
(137, 'admin', '2026-08-08 13:10:48', NULL, 1, NULL),
(138, 'Ajh12', '2026-08-08 14:39:26', NULL, 0, 'Invalid credentials'),
(139, 'admin', '2026-08-08 14:39:51', NULL, 1, NULL),
(140, 'admin', '2026-08-08 14:42:57', NULL, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `officers`
--

CREATE TABLE `officers` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `badge_number` varchar(50) DEFAULT NULL,
  `officer_rank` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `station_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `officers`
--

INSERT INTO `officers` (`id`, `user_id`, `badge_number`, `officer_rank`, `is_active`, `station_id`) VALUES
(1, 2, '202600001', 'INSPECTOR', 1, 1),
(2, 3, '202600002', 'INSPECTOR', 1, 1),
(3, 5, '202600003', 'INSPECTOR', 1, 1),
(4, 8, '202600004', 'INSPECTOR', 1, 3),
(5, 9, '202600005', 'INSPECTOR', 1, 3),
(6, 12, '202600006', 'CONSTABLE', 1, 1),
(7, 13, '202600007', 'INSPECTOR', 1, 3);

--
-- Triggers `officers`
--
DELIMITER $$
CREATE TRIGGER `officers_after_insert` AFTER INSERT ON `officers` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'officers', NEW.badge_number,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('User ID', 20, ' '), ': ', IFNULL(NEW.user_id, 'N/A'), '\n',
            RPAD('Badge Number', 20, ' '), ': ', IFNULL(NEW.badge_number, 'N/A'), '\n',
            RPAD('Rank', 20, ' '), ': ', IFNULL(NEW.officer_rank, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `officers_after_update` AFTER UPDATE ON `officers` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'officers', NEW.badge_number,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('User ID', 20, ' '), ': ', IFNULL(OLD.user_id, 'N/A'), '\n',
            RPAD('Badge Number', 20, ' '), ': ', IFNULL(OLD.badge_number, 'N/A'), '\n',
            RPAD('Rank', 20, ' '), ': ', IFNULL(OLD.officer_rank, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(OLD.station_id, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('User ID', 20, ' '), ': ', IFNULL(NEW.user_id, 'N/A'), '\n',
            RPAD('Badge Number', 20, ' '), ': ', IFNULL(NEW.badge_number, 'N/A'), '\n',
            RPAD('Rank', 20, ' '), ': ', IFNULL(NEW.officer_rank, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `police_stations`
--

CREATE TABLE `police_stations` (
  `id` int(11) NOT NULL,
  `station_code` varchar(100) NOT NULL,
  `station_name` varchar(100) NOT NULL,
  `address` text DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `police_stations`
--

INSERT INTO `police_stations` (`id`, `station_code`, `station_name`, `address`, `phone`, `is_active`) VALUES
(1, 'CA1', 'CENTRAL AHMEDABAD 1', 'lal darvaja,ahmedabad', '9999999999', 1),
(2, 'CG', 'Central Gujarat', 'patan,gujarat', '9547563215', 1),
(3, 'KPS', 'kheda police station', 'kheda', '9874563546', 1);

--
-- Triggers `police_stations`
--
DELIMITER $$
CREATE TRIGGER `police_stations_after_insert` AFTER INSERT ON `police_stations` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'police_stations', NEW.station_name,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Station Code', 20, ' '), ': ', IFNULL(NEW.station_code, 'N/A'), '\n',
            RPAD('Station Name', 20, ' '), ': ', IFNULL(NEW.station_name, 'N/A'), '\n',
            RPAD('Address', 20, ' '), ': ', IFNULL(NEW.address, 'N/A'), '\n',
            RPAD('Phone', 20, ' '), ': ', IFNULL(NEW.phone, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `police_stations_after_update` AFTER UPDATE ON `police_stations` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'police_stations', NEW.station_name,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('Station Code', 20, ' '), ': ', IFNULL(OLD.station_code, 'N/A'), '\n',
            RPAD('Station Name', 20, ' '), ': ', IFNULL(OLD.station_name, 'N/A'), '\n',
            RPAD('Address', 20, ' '), ': ', IFNULL(OLD.address, 'N/A'), '\n',
            RPAD('Phone', 20, ' '), ': ', IFNULL(OLD.phone, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Station Code', 20, ' '), ': ', IFNULL(NEW.station_code, 'N/A'), '\n',
            RPAD('Station Name', 20, ' '), ': ', IFNULL(NEW.station_name, 'N/A'), '\n',
            RPAD('Address', 20, ' '), ': ', IFNULL(NEW.address, 'N/A'), '\n',
            RPAD('Phone', 20, ' '), ': ', IFNULL(NEW.phone, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `station_id` int(11) DEFAULT NULL,
  `station_code` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`id`, `user_id`, `employee_id`, `is_active`, `station_id`, `station_code`) VALUES
(1, 6, '202600001', 1, 1, 'CA1'),
(2, 10, '202600002', 1, 3, 'KPS'),
(3, 11, '202600003', 1, 3, 'KPS');

--
-- Triggers `staff`
--
DELIMITER $$
CREATE TRIGGER `staff_after_insert` AFTER INSERT ON `staff` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'staff', NEW.employee_id,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('User ID', 20, ' '), ': ', IFNULL(NEW.user_id, 'N/A'), '\n',
            RPAD('Employee ID', 20, ' '), ': ', IFNULL(NEW.employee_id, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Station Code', 20, ' '), ': ', IFNULL(NEW.station_code, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `staff_after_update` AFTER UPDATE ON `staff` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'staff', NEW.employee_id,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('User ID', 20, ' '), ': ', IFNULL(OLD.user_id, 'N/A'), '\n',
            RPAD('Employee ID', 20, ' '), ': ', IFNULL(OLD.employee_id, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(OLD.station_id, 'N/A'), '\n',
            RPAD('Station Code', 20, ' '), ': ', IFNULL(OLD.station_code, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('User ID', 20, ' '), ': ', IFNULL(NEW.user_id, 'N/A'), '\n',
            RPAD('Employee ID', 20, ' '), ': ', IFNULL(NEW.employee_id, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Station Code', 20, ' '), ': ', IFNULL(NEW.station_code, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `staff_before_insert` BEFORE INSERT ON `staff` FOR EACH ROW BEGIN
    DECLARE code VARCHAR(50);
    SELECT station_code INTO code FROM police_stations WHERE id = NEW.station_id;
    SET NEW.station_code = IFNULL(code, 'UNKNOWN');
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `staff_before_update` BEFORE UPDATE ON `staff` FOR EACH ROW BEGIN
    DECLARE code VARCHAR(50);
    SELECT station_code INTO code FROM police_stations WHERE id = NEW.station_id;
    SET NEW.station_code = IFNULL(code, 'UNKNOWN');
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  `station_id` int(11) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `active` tinyint(1) DEFAULT 1,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `station_id`, `full_name`, `active`, `email`) VALUES
(1, 'admin', '1Admin@_', 'ADMIN', 1, 'system admin', 1, 'ADMIN202600001@police.gov.in'),
(2, 'inspector_raj', '000000', 'OFFICER', 1, 'raj pratap mahol', 1, 'OFFICER202600001@police.gov.in'),
(3, 'officer_rahul', '000000', 'OFFICER', 1, 'rahul amrwadi', 1, 'OFFICER202600002@police.gov.in'),
(4, 'Super', '123', 'ADMIN', 2, 'Super Admin', 1, 'ADMIN202600002@police.gov.in'),
(5, 'officer_aman', '111111', 'OFFICER', 1, 'aman yadav', 1, 'OFFICER202600003@police.gov.in'),
(6, 'staff_arham', '111111', 'STAFF', 1, 'arham shaikh', 1, 'STAFF202600001@police.gov.in'),
(7, 'admin_2', '000000', 'ADMIN', 3, 'admin two', 1, NULL),
(8, 'officer_john', '000000', 'OFFICER', 3, 'John Doe', 1, 'OFFICER202600004@police.gov.in'),
(9, 'officer_smith', '000000', 'OFFICER', 3, 'Jane Smith', 1, 'OFFICER202600005@police.gov.in'),
(10, 'staff_alice', '000000', 'STAFF', 3, 'alice', 1, 'STAFF202600002@police.gov.in'),
(11, 'staff_bob', '000000', 'STAFF', 3, 'bob', 1, 'STAFF202600003@police.gov.in'),
(12, 'trial', '000000', 'OFFICER', 1, 'trial', 1, 'OFFICER202600006@police.gov.in'),
(13, 'officer_jerry', '000000', 'OFFICER', 3, 'jerry hill', 1, 'OFFICER202600007@police.gov.in'),
(14, 'Super2', '000000', 'ADMIN', 2, 'super admin', 1, NULL),
(15, '213', '000000', 'ADMIN', 3, 'raj', 1, NULL),
(16, 'admin_3', '000000', 'ADMIN', 1, 'trial', 1, NULL);

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `sync_user_active_to_children_after_insert` AFTER INSERT ON `users` FOR EACH ROW BEGIN
    -- If the user is an officer, ensure the officer record gets the active status
    IF NEW.role = 'OFFICER' THEN
        UPDATE officers SET is_active = NEW.active WHERE user_id = NEW.id;
    END IF;
    -- If the user is staff, update staff record
    IF NEW.role = 'STAFF' THEN
        UPDATE staff SET is_active = NEW.active WHERE user_id = NEW.id;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `sync_user_active_to_children_after_update` AFTER UPDATE ON `users` FOR EACH ROW BEGIN
    -- Update officer
    IF NEW.role = 'OFFICER' THEN
        UPDATE officers SET is_active = NEW.active WHERE user_id = NEW.id;
    END IF;
    -- Update staff
    IF NEW.role = 'STAFF' THEN
        UPDATE staff SET is_active = NEW.active WHERE user_id = NEW.id;
    END IF;
    -- Update admin
    IF NEW.role = 'ADMIN' THEN
        UPDATE admins SET is_active = NEW.active WHERE user_id = NEW.id;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `users_after_insert` AFTER INSERT ON `users` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'users', NEW.username,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Username', 20, ' '), ': ', IFNULL(NEW.username, 'N/A'), '\n',
            RPAD('Role', 20, ' '), ': ', IFNULL(NEW.role, 'N/A'), '\n',
            RPAD('Full Name', 20, ' '), ': ', IFNULL(NEW.full_name, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `users_after_update` AFTER UPDATE ON `users` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'users', NEW.username,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('Username', 20, ' '), ': ', IFNULL(OLD.username, 'N/A'), '\n',
            RPAD('Role', 20, ' '), ': ', IFNULL(OLD.role, 'N/A'), '\n',
            RPAD('Full Name', 20, ' '), ': ', IFNULL(OLD.full_name, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(OLD.station_id, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('Username', 20, ' '), ': ', IFNULL(NEW.username, 'N/A'), '\n',
            RPAD('Role', 20, ' '), ': ', IFNULL(NEW.role, 'N/A'), '\n',
            RPAD('Full Name', 20, ' '), ': ', IFNULL(NEW.full_name, 'N/A'), '\n',
            RPAD('Station ID', 20, ' '), ': ', IFNULL(NEW.station_id, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `victims`
--

CREATE TABLE `victims` (
  `id` int(11) NOT NULL,
  `fir_number` varchar(50) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `harm_description` longtext DEFAULT NULL,
  `contact` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `victims`
--

INSERT INTO `victims` (`id`, `fir_number`, `first_name`, `last_name`, `harm_description`, `contact`, `address`, `is_active`) VALUES
(1, 'FIR-CA1-1', 'raju', 'taklu', NULL, '9874563214', 'dholakpur', 1),
(2, 'FIR-CA1-1', 'jaggu', 'bandar', NULL, '9856321475', 'dholakpur', 1),
(3, 'FIR-KPS-2', 'shivam', 'thakkar', 'serious injury', '9874563215', 'ahmedabad', 1),
(4, 'FIR-KPS-3', 'trial', 'triall', 'uhgy', '9874563210', 'kheda', 1),
(5, 'FIR-KPS-3', 'fgh', 'sdfg', 'ggfdh', '9874563210', 'dhdf', 1);

--
-- Triggers `victims`
--
DELIMITER $$
CREATE TRIGGER `victims_after_delete` AFTER DELETE ON `victims` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'DELETE', 'victims', OLD.id,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(OLD.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(OLD.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        'DELETED',
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `victims_after_insert` AFTER INSERT ON `victims` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'victims', NEW.id,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(NEW.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(NEW.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `victims_after_update` AFTER UPDATE ON `victims` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'victims', NEW.id,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(OLD.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(OLD.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(NEW.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(NEW.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `witnesses`
--

CREATE TABLE `witnesses` (
  `id` int(11) NOT NULL,
  `fir_number` varchar(50) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `contact` varchar(20) DEFAULT NULL,
  `statement` text DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `witnesses`
--

INSERT INTO `witnesses` (`id`, `fir_number`, `first_name`, `last_name`, `contact`, `statement`, `is_active`) VALUES
(1, 'FIR-CA1-1', 'jaggu', 'bandar', '9856321478', 'i saw that the truck driver was drunk and was driving at 130km/h speed', 1),
(2, 'FIR-KPS-2', 'parth', 'patel', '9856472315', 'witness the whole scenc', 1);

--
-- Triggers `witnesses`
--
DELIMITER $$
CREATE TRIGGER `witnesses_after_delete` AFTER DELETE ON `witnesses` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'DELETE', 'witnesses', OLD.id,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(OLD.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(OLD.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        'DELETED',
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `witnesses_after_insert` AFTER INSERT ON `witnesses` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'INSERT', 'witnesses', NEW.id,
        NULL,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(NEW.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(NEW.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `witnesses_after_update` AFTER UPDATE ON `witnesses` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value, timestamp)
    VALUES (
        @app_user_id, @app_username, 'UPDATE', 'witnesses', NEW.id,
        CONCAT(
            RPAD('ID', 20, ' '), ': ', OLD.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(OLD.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(OLD.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(OLD.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(OLD.is_active = 1, 'Yes', 'No')
        ),
        CONCAT(
            RPAD('ID', 20, ' '), ': ', NEW.id, '\n',
            RPAD('FIR Number', 20, ' '), ': ', IFNULL(NEW.fir_number, 'N/A'), '\n',
            RPAD('First Name', 20, ' '), ': ', IFNULL(NEW.first_name, 'N/A'), '\n',
            RPAD('Last Name', 20, ' '), ': ', IFNULL(NEW.last_name, 'N/A'), '\n',
            RPAD('Active', 20, ' '), ': ', IF(NEW.is_active = 1, 'Yes', 'No')
        ),
        NOW()
    );
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `crime_categories`
--
ALTER TABLE `crime_categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `category` (`category`);

--
-- Indexes for table `crime_criminal_mapping`
--
ALTER TABLE `crime_criminal_mapping`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_link` (`crime_number`,`criminal_id`,`role`),
  ADD KEY `criminal_id` (`criminal_id`);

--
-- Indexes for table `crime_records`
--
ALTER TABLE `crime_records`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `crime_number` (`crime_number`),
  ADD KEY `fir_number` (`fir_number`),
  ADD KEY `idx_crime_status` (`status`);

--
-- Indexes for table `criminals`
--
ALTER TABLE `criminals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_criminals_name` (`first_name`,`last_name`),
  ADD KEY `idx_criminals_wanted` (`wanted_status`);

--
-- Indexes for table `evidence`
--
ALTER TABLE `evidence`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `evidence_number` (`evidence_number`),
  ADD KEY `fir_number` (`fir_number`),
  ADD KEY `crime_number` (`crime_number`);

--
-- Indexes for table `fir`
--
ALTER TABLE `fir`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `fir_number` (`fir_number`),
  ADD KEY `assigned_officer_id` (`assigned_officer_id`),
  ADD KEY `station_id` (`station_id`),
  ADD KEY `idx_fir_complainant` (`complainant_name`),
  ADD KEY `idx_fir_status` (`status`);

--
-- Indexes for table `fir_crime_categories`
--
ALTER TABLE `fir_crime_categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `fir_id` (`fir_id`,`category_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `investigations`
--
ALTER TABLE `investigations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `fir_number` (`fir_number`),
  ADD KEY `officer_id` (`officer_id`);

--
-- Indexes for table `login_history`
--
ALTER TABLE `login_history`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `officers`
--
ALTER TABLE `officers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `badge_number` (`badge_number`),
  ADD KEY `fk_officers_station` (`station_id`);

--
-- Indexes for table `police_stations`
--
ALTER TABLE `police_stations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `station_name` (`station_name`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `employee_id` (`employee_id`),
  ADD KEY `fk_staff_station` (`station_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `station_id` (`station_id`);

--
-- Indexes for table `victims`
--
ALTER TABLE `victims`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fir_number` (`fir_number`);

--
-- Indexes for table `witnesses`
--
ALTER TABLE `witnesses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fir_number` (`fir_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=186;

--
-- AUTO_INCREMENT for table `crime_categories`
--
ALTER TABLE `crime_categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `crime_criminal_mapping`
--
ALTER TABLE `crime_criminal_mapping`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `crime_records`
--
ALTER TABLE `crime_records`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `criminals`
--
ALTER TABLE `criminals`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `evidence`
--
ALTER TABLE `evidence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `fir`
--
ALTER TABLE `fir`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `fir_crime_categories`
--
ALTER TABLE `fir_crime_categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `investigations`
--
ALTER TABLE `investigations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `login_history`
--
ALTER TABLE `login_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=141;

--
-- AUTO_INCREMENT for table `officers`
--
ALTER TABLE `officers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `police_stations`
--
ALTER TABLE `police_stations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `victims`
--
ALTER TABLE `victims`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `witnesses`
--
ALTER TABLE `witnesses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `fk_admin_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `crime_criminal_mapping`
--
ALTER TABLE `crime_criminal_mapping`
  ADD CONSTRAINT `crime_criminal_mapping_ibfk_1` FOREIGN KEY (`crime_number`) REFERENCES `crime_records` (`crime_number`),
  ADD CONSTRAINT `crime_criminal_mapping_ibfk_2` FOREIGN KEY (`criminal_id`) REFERENCES `criminals` (`id`);

--
-- Constraints for table `crime_records`
--
ALTER TABLE `crime_records`
  ADD CONSTRAINT `crime_records_ibfk_1` FOREIGN KEY (`fir_number`) REFERENCES `fir` (`fir_number`);

--
-- Constraints for table `evidence`
--
ALTER TABLE `evidence`
  ADD CONSTRAINT `evidence_ibfk_1` FOREIGN KEY (`fir_number`) REFERENCES `fir` (`fir_number`),
  ADD CONSTRAINT `evidence_ibfk_2` FOREIGN KEY (`crime_number`) REFERENCES `crime_records` (`crime_number`);

--
-- Constraints for table `fir`
--
ALTER TABLE `fir`
  ADD CONSTRAINT `fir_ibfk_1` FOREIGN KEY (`assigned_officer_id`) REFERENCES `officers` (`id`),
  ADD CONSTRAINT `fir_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `police_stations` (`id`);

--
-- Constraints for table `fir_crime_categories`
--
ALTER TABLE `fir_crime_categories`
  ADD CONSTRAINT `fir_crime_categories_ibfk_1` FOREIGN KEY (`fir_id`) REFERENCES `fir` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fir_crime_categories_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `crime_categories` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `investigations`
--
ALTER TABLE `investigations`
  ADD CONSTRAINT `investigations_ibfk_1` FOREIGN KEY (`fir_number`) REFERENCES `fir` (`fir_number`),
  ADD CONSTRAINT `investigations_ibfk_2` FOREIGN KEY (`officer_id`) REFERENCES `officers` (`id`);

--
-- Constraints for table `officers`
--
ALTER TABLE `officers`
  ADD CONSTRAINT `fk_officers_station` FOREIGN KEY (`station_id`) REFERENCES `police_stations` (`id`),
  ADD CONSTRAINT `officers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `fk_staff_station` FOREIGN KEY (`station_id`) REFERENCES `police_stations` (`id`),
  ADD CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `police_stations` (`id`);

--
-- Constraints for table `victims`
--
ALTER TABLE `victims`
  ADD CONSTRAINT `victims_ibfk_1` FOREIGN KEY (`fir_number`) REFERENCES `fir` (`fir_number`);

--
-- Constraints for table `witnesses`
--
ALTER TABLE `witnesses`
  ADD CONSTRAINT `witnesses_ibfk_1` FOREIGN KEY (`fir_number`) REFERENCES `fir` (`fir_number`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
