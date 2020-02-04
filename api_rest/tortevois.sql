-- phpMyAdmin SQL Dump
-- version 4.7.3
-- https://www.phpmyadmin.net/
--
-- Hôte : tortevoisbdd.mysql.db
-- Généré le :  Dim 23 juin 2019 à 12:20
-- Version du serveur :  5.5.60-0+deb7u1-log
-- Version de PHP :  5.6.40-0+deb8u2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `tortevois`
--

-- --------------------------------------------------------

--
-- Structure de la table `smb116_customer`
--

CREATE TABLE `smb116_customer` (
  `id_customer` int(11) NOT NULL,
  `contract_id` varchar(255) NOT NULL,
  `contract_key` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `smb116_equipment`
--

CREATE TABLE `smb116_equipment` (
  `id_equipment` int(11) NOT NULL,
  `id_customer` int(11) NOT NULL,
  `serial_number` varchar(255) NOT NULL,
  `comment` text NOT NULL,
  `lat` double NOT NULL,
  `lng` double NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `smb116_message`
--

CREATE TABLE `smb116_message` (
  `id_message` int(11) NOT NULL,
  `id_ticket` int(11) NOT NULL,
  `id_customer` int(11) NOT NULL,
  `date_message` datetime NOT NULL,
  `content` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `smb116_ticket`
--

CREATE TABLE `smb116_ticket` (
  `id_ticket` int(11) NOT NULL,
  `id_equipment` int(11) NOT NULL,
  `subject` text NOT NULL,
  `status` tinyint(1) DEFAULT '0',
  `date_status` datetime NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `smb116_customer`
--
ALTER TABLE `smb116_customer`
  ADD PRIMARY KEY (`id_customer`);

--
-- Index pour la table `smb116_equipment`
--
ALTER TABLE `smb116_equipment`
  ADD PRIMARY KEY (`id_equipment`),
  ADD KEY `fk_id_customer` (`id_customer`);

--
-- Index pour la table `smb116_message`
--
ALTER TABLE `smb116_message`
  ADD PRIMARY KEY (`id_message`),
  ADD KEY `fk_id_ticket` (`id_ticket`),
  ADD KEY `fk_id_customer` (`id_customer`);

--
-- Index pour la table `smb116_ticket`
--
ALTER TABLE `smb116_ticket`
  ADD PRIMARY KEY (`id_ticket`),
  ADD KEY `fk_id_equipment` (`id_equipment`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `smb116_customer`
--
ALTER TABLE `smb116_customer`
  MODIFY `id_customer` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
--
-- AUTO_INCREMENT pour la table `smb116_equipment`
--
ALTER TABLE `smb116_equipment`
  MODIFY `id_equipment` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
--
-- AUTO_INCREMENT pour la table `smb116_message`
--
ALTER TABLE `smb116_message`
  MODIFY `id_message` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
--
-- AUTO_INCREMENT pour la table `smb116_ticket`
--
ALTER TABLE `smb116_ticket`
  MODIFY `id_ticket` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
