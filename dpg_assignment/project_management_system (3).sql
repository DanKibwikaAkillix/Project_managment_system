-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 15, 2024 at 05:46 PM
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
-- Database: `project_management_system`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddFile` (IN `p_fileName` VARCHAR(255), IN `p_action` VARCHAR(50), IN `p_fileType` VARCHAR(50), IN `p_findingName` VARCHAR(255), IN `p_findingType` VARCHAR(50), IN `p_description` TEXT, IN `p_filePath` VARCHAR(255), IN `p_uploadedBy` VARCHAR(255))   BEGIN
    INSERT INTO files (fileName, action, fileType, findingName, findingType, description, filePath, uploadedBy)
    VALUES (p_fileName, p_action, p_fileType, p_findingName, p_findingType, p_description, p_filePath, p_uploadedBy);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `AddTeamMember` (IN `p_memberName` VARCHAR(255), IN `p_memberEmail` VARCHAR(255), IN `p_projectName` VARCHAR(255), IN `p_userEmail` VARCHAR(255))   BEGIN
    INSERT INTO Team (memberName, memberEmail, projectName, email)
    VALUES (p_memberName, p_memberEmail, p_projectName, p_userEmail);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CountProjectsByUserEmail` (IN `userEmail` VARCHAR(255))   BEGIN
    SELECT 
        COUNT(*) AS totalProjects,
        SUM(CASE WHEN projectStatus = 'ongoing' THEN 1 ELSE 0 END) AS ongoingProjects,
        SUM(CASE WHEN projectStatus = 'completed' THEN 1 ELSE 0 END) AS completedProjects
    FROM 
        project
    WHERE 
        email = userEmail;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteFile` (IN `p_id` INT)   BEGIN
    DELETE FROM files WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllFiles` ()   BEGIN
    SELECT * FROM files;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetFileById` (IN `p_id` INT)   BEGIN
    SELECT * FROM files WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetProjectsByUserEmail` (IN `userEmail` VARCHAR(255))   BEGIN
    SELECT projectName 
    FROM Project 
    WHERE email = userEmail;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetUserByEmail` (IN `input_email` VARCHAR(100))   BEGIN
    SELECT userID, userName, emailAddress, password, accountStatus
    FROM Users
    WHERE emailAddress = input_email;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetUserNameByEmail` (IN `userEmail` VARCHAR(255))   BEGIN
    SELECT* FROM users WHERE  emailAddress = userEmail;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetUserNameByID` (IN `userID` INT)   BEGIN
    SELECT userName FROM users WHERE userID = userID;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertFileRecord` (IN `p_fileName` VARCHAR(255), IN `p_fileType` VARCHAR(50), IN `p_filePath` VARCHAR(255), IN `p_uploadedBy` VARCHAR(100), IN `p_uploadDate` DATETIME)   BEGIN
    INSERT INTO files (fileName, fileType, filePath, uploadedBy, uploadDate)
    VALUES (p_fileName, p_fileType, p_filePath, p_uploadedBy, p_uploadDate);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `RegisterUser` (IN `p_userName` VARCHAR(50), IN `p_emailAddress` VARCHAR(100), IN `p_password` VARCHAR(255), IN `p_roleID` INT, IN `p_firstName` VARCHAR(50), IN `p_lastName` VARCHAR(50))   BEGIN
    INSERT INTO Users (userName, emailAddress, password, roleID, firstName, lastName)
    VALUES (p_userName, p_emailAddress, p_password, p_roleID, p_firstName, p_lastName);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateFile` (IN `p_id` INT, IN `p_fileName` VARCHAR(255), IN `p_action` VARCHAR(50), IN `p_fileType` VARCHAR(50), IN `p_findingName` VARCHAR(255), IN `p_findingType` VARCHAR(50), IN `p_description` TEXT, IN `p_filePath` VARCHAR(255), IN `p_uploadedBy` VARCHAR(255))   BEGIN
    UPDATE files 
    SET fileName = p_fileName, 
        action = p_action, 
        fileType = p_fileType, 
        findingName = p_findingName, 
        findingType = p_findingType, 
        description = p_description, 
        filePath = p_filePath, 
        uploadedBy = p_uploadedBy
    WHERE id = p_id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `files`
--

CREATE TABLE `files` (
  `id` int(11) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `action` varchar(50) DEFAULT NULL,
  `fileType` varchar(50) DEFAULT NULL,
  `findingName` varchar(255) DEFAULT NULL,
  `findingType` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `filePath` varchar(255) NOT NULL,
  `uploadedBy` varchar(255) NOT NULL,
  `uploadDate` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `files`
--

INSERT INTO `files` (`id`, `fileName`, `action`, `fileType`, `findingName`, `findingType`, `description`, `filePath`, `uploadedBy`, `uploadDate`) VALUES
(2, 'Database Programming Project Structure 2024.pdf', NULL, 'application/pdf', NULL, NULL, NULL, 'File_folder/Database Programming Project Structure 2024.pdf', 'user@gmail.com', '2024-10-15 17:14:15');

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `projectID` int(11) NOT NULL,
  `projectName` varchar(30) DEFAULT NULL,
  `projectDescription` varchar(30) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `projectStatus` varchar(30) DEFAULT NULL,
  `UserID` int(11) DEFAULT NULL,
  `findingID` int(11) NOT NULL,
  `email` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`projectID`, `projectName`, `projectDescription`, `startDate`, `endDate`, `projectStatus`, `UserID`, `findingID`, `email`) VALUES
(1, 'Flutter App demo', 'Demo of the App on the 12th', '2024-10-15', '2024-10-25', 'Done', NULL, 0, ''),
(2, 'Flutter App demo', 'fdfd', '2024-10-15', '2024-10-17', 'Pending', NULL, 0, ''),
(3, 'Java project', 'Jave is a very nice language', '2024-10-15', '2024-10-15', 'On going', NULL, 0, ''),
(4, 'Java project', 'Jave is a very nice language', '2024-10-15', '2024-10-15', 'On going', NULL, 0, ''),
(5, 'Java project', 'Jave is a very nice language', '2024-10-15', '2024-10-15', 'On going', NULL, 0, 'dankibwika821@gmail.com'),
(6, 'Meeting with Client', 'The client is really important', '2024-10-15', '2024-11-01', 'On going', NULL, 0, 'dankibwika821@gmail.com'),
(7, 'First project', 'This is my description', '2024-10-15', '2024-10-25', 'On going', NULL, 0, 'user@gmail.com');

-- --------------------------------------------------------

--
-- Table structure for table `team`
--

CREATE TABLE `team` (
  `ID` int(11) NOT NULL,
  `memberName` varchar(255) DEFAULT NULL,
  `memberEmail` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `userlogs`
--

CREATE TABLE `userlogs` (
  `logID` int(11) NOT NULL,
  `userID` int(11) DEFAULT NULL,
  `action` varchar(100) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userID` int(11) NOT NULL,
  `userName` varchar(50) NOT NULL,
  `emailAddress` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `roleID` int(11) NOT NULL,
  `accountStatus` enum('active','inactive','suspended') DEFAULT 'active',
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT current_timestamp(),
  `updatedAt` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userID`, `userName`, `emailAddress`, `password`, `roleID`, `accountStatus`, `firstName`, `lastName`, `createdAt`, `updatedAt`) VALUES
(1, 'johnDoe', 'johndoe@example.com', 'password123', 1, 'active', 'John', 'Doe', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(2, 'janeSmith', 'janesmith@example.com', 'securePass!', 2, 'active', 'Jane', 'Smith', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(3, 'michaelBrown', 'michael.brown@example.com', 'mikePass456', 1, 'suspended', 'Michael', 'Brown', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(4, 'emilyDavis', 'emily.davis@example.com', 'emilyPass789', 2, 'inactive', 'Emily', 'Davis', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(5, 'robertJones', 'robert.jones@example.com', 'robert12345', 3, 'active', 'Robert', 'Jones', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(6, 'lisaWhite', 'lisa.white@example.com', 'lisaSafe99', 3, 'active', 'Lisa', 'White', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(7, 'davidWilson', 'david.wilson@example.com', 'davidPass098', 2, 'active', 'David', 'Wilson', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(8, 'susanTaylor', 'susan.taylor@example.com', 'susPass123!', 1, 'inactive', 'Susan', 'Taylor', '2024-10-14 23:16:43', '2024-10-14 23:16:43'),
(9, 'Kiyo', 'dankibwika821@gmail.com', '$2y$10$WFI6APKq1TvDCoDTWp0UceR/hCYgkCNc.ncHXE.VI9Fb4RoHFxbZu', 221012273, 'active', 'Dan', 'Kibwika', '2024-10-15 06:44:48', '2024-10-15 06:44:48'),
(12, 'Username1', 'user@gmail.com', '$2y$10$bX/fpd.bU1QdteEaoUF/be3fsNnYKSvA15pLFzeCyJtWR8qGG.g1i', 887764, 'active', 'User1', 'UserLastname', '2024-10-15 10:25:48', '2024-10-15 10:25:48');

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `after_user_status_update` AFTER UPDATE ON `users` FOR EACH ROW BEGIN
    IF OLD.accountStatus != NEW.accountStatus THEN
        INSERT INTO UserStatusLog (userID, oldStatus, newStatus) 
        VALUES (NEW.userID, OLD.accountStatus, NEW.accountStatus);
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `before_user_update` BEFORE UPDATE ON `users` FOR EACH ROW BEGIN
    INSERT INTO UserLogs (userID, action, timestamp) 
    VALUES (OLD.userID, 'User Updated', NOW());
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `files`
--
ALTER TABLE `files`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`projectID`),
  ADD KEY `idx_project_userID` (`UserID`),
  ADD KEY `idx_project_projectStatus` (`projectStatus`);

--
-- Indexes for table `team`
--
ALTER TABLE `team`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `userlogs`
--
ALTER TABLE `userlogs`
  ADD PRIMARY KEY (`logID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userID`),
  ADD UNIQUE KEY `emailAddress` (`emailAddress`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `projectID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `team`
--
ALTER TABLE `team`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `userlogs`
--
ALTER TABLE `userlogs`
  MODIFY `logID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `project`
--
ALTER TABLE `project`
  ADD CONSTRAINT `project_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `users` (`userID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
