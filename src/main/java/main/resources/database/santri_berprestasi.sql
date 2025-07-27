-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for santri_berprestasi
DROP DATABASE IF EXISTS `santri_berprestasi`;
CREATE DATABASE IF NOT EXISTS `santri_berprestasi` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `santri_berprestasi`;

-- Dumping structure for table santri_berprestasi.penilaian
DROP TABLE IF EXISTS `penilaian`;
CREATE TABLE IF NOT EXISTS `penilaian` (
  `id` int NOT NULL AUTO_INCREMENT,
  `santri_id` int NOT NULL,
  `nilai_raport` decimal(5,2) NOT NULL,
  `nilai_akhlak` decimal(5,2) NOT NULL,
  `nilai_ekstrakurikuler` decimal(5,2) NOT NULL,
  `nilai_absensi` decimal(5,2) NOT NULL,
  `skor_saw` decimal(10,6) DEFAULT '0.000000',
  `ranking` int DEFAULT '0',
  `created_by` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `santri_id` (`santri_id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `penilaian_ibfk_1` FOREIGN KEY (`santri_id`) REFERENCES `santri` (`id`) ON DELETE CASCADE,
  CONSTRAINT `penilaian_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table santri_berprestasi.penilaian: ~9 rows (approximately)
REPLACE INTO `penilaian` (`id`, `santri_id`, `nilai_raport`, `nilai_akhlak`, `nilai_ekstrakurikuler`, `nilai_absensi`, `skor_saw`, `ranking`, `created_by`, `created_at`, `updated_at`) VALUES
	(1, 1, 85.50, 90.00, 88.00, 95.00, 0.915871, 4, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(2, 2, 92.00, 95.00, 85.00, 90.00, 0.947612, 2, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(3, 3, 78.00, 85.00, 90.00, 85.00, 0.862527, 8, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(4, 4, 88.00, 92.00, 87.00, 93.00, 0.928550, 3, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(5, 5, 82.00, 88.00, 83.00, 88.00, 0.877508, 6, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(6, 6, 94.00, 96.00, 92.00, 98.00, 0.981633, 1, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(7, 7, 80.00, 82.00, 85.00, 87.00, 0.853691, 9, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(8, 8, 86.00, 89.00, 84.00, 91.00, 0.902692, 5, 1, '2025-07-20 17:11:08', '2025-07-24 07:09:58'),
	(9, 9, 67.00, 98.00, 97.00, 90.00, 0.874902, 7, 5, '2025-07-23 03:05:24', '2025-07-24 07:09:58'),
	(10, 10, 76.00, 67.00, 98.00, 96.00, 0.826465, 10, 1, '2025-07-24 07:09:32', '2025-07-24 07:09:58'),
	(11, 11, 12.00, 12.00, 12.00, 12.00, 0.124533, 11, 1, '2025-07-27 16:10:54', '2025-07-27 16:11:15');

-- Dumping structure for table santri_berprestasi.santri
DROP TABLE IF EXISTS `santri`;
CREATE TABLE IF NOT EXISTS `santri` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nama` varchar(100) NOT NULL,
  `kelas` varchar(20) NOT NULL,
  `tahun_ajaran` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table santri_berprestasi.santri: ~9 rows (approximately)
REPLACE INTO `santri` (`id`, `nama`, `kelas`, `tahun_ajaran`, `created_at`) VALUES
	(1, 'Ahmad Fauzi', 'X IPA 1', '2024/2025', '2025-07-20 17:10:44'),
	(2, 'Siti Aisyah', 'X IPA 1', '2024/2025', '2025-07-20 17:10:44'),
	(3, 'Muhammad Rizki', 'X IPA 2', '2024/2025', '2025-07-20 17:10:44'),
	(4, 'Fatimah Zahra', 'XI IPS 1', '2024/2025', '2025-07-20 17:10:44'),
	(5, 'Abdullah Rahman', 'XI IPS 2', '2024/2025', '2025-07-20 17:10:44'),
	(6, 'Khadijah Aminah', 'XII IPA 1', '2024/2025', '2025-07-20 17:10:44'),
	(7, 'Usman bin Affan', 'XII IPA 2', '2024/2025', '2025-07-20 17:10:44'),
	(8, 'Aisha Siddiqa', 'X IPS 1', '2024/2025', '2025-07-20 17:10:44'),
	(9, 'Julian marcell', 'XII IPA 1', '2024/2025', '2025-07-20 17:22:22'),
	(10, 'Irsyad Hamdan', 'X IPA 1', '2024/2025', '2025-07-24 07:09:14'),
	(11, 'Ayudiyah Pratiwi', 'X IPA 2', '2024/2025', '2025-07-27 16:10:42');

-- Dumping structure for table santri_berprestasi.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','guru') DEFAULT 'guru',
  `nama_lengkap` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table santri_berprestasi.users: ~3 rows (approximately)
REPLACE INTO `users` (`id`, `username`, `password`, `role`, `nama_lengkap`, `created_at`) VALUES
	(1, 'admin', 'password', 'admin', 'Administrator', '2025-07-20 16:53:09'),
	(5, 'guru1', 'password', 'guru', 'Ustadz Ahmad', '2025-07-20 17:10:28'),
	(6, 'guru2', 'password', 'guru', 'Ustadzah Fatimah', '2025-07-20 17:10:28');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
