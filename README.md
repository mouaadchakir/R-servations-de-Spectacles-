# Système de Réservation de Spectacles avec Spring Boot

Ce projet est un système complet de réservation de billets de spectacles construit avec Spring Boot. Il permet aux utilisateurs de rechercher des spectacles, réserver des billets, effectuer des paiements et télécharger leurs tickets.

## Fonctionnalités

- Authentification et gestion des utilisateurs
- Gestion des spectacles et des catégories
- Système de réservation de sièges
- Intégration de paiement avec Stripe
- Génération de tickets PDF avec QR code
- Interface administrateur pour la gestion des spectacles

## Technologies utilisées

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Thymeleaf
- Bootstrap 5
- MySQL
- Lombok
- Flying Saucer (PDF generation)
- ZXing (QR code generation)
- Stripe API

## Configuration requise

- Java 17+
- Maven
- MySQL

## Installation

1. Cloner le dépôt
2. Configurer la base de données MySQL dans `application.properties`
3. Ajouter les clés API Stripe dans `application.properties`
4. Lancer l'application avec Maven:

```
mvn spring:boot run
```

## Structure du projet

- `model` - Les entités de domaine (User, Show, Seat, Reservation)
- `repository` - Les interfaces de dépôt pour interagir avec la base de données
- `service` - La couche service avec la logique métier
- `controller` - Les contrôleurs qui gèrent les requêtes HTTP
- `config` - Les classes de configuration
- `resources/templates` - Les templates Thymeleaf pour le front-end

## Endpoints API

### Utilisateurs
- GET/POST `/register` - Inscription
- GET `/login` - Connexion
- GET/POST `/profile` - Profil utilisateur
- GET/POST `/change-password` - Changement de mot de passe

### Spectacles
- GET `/shows` - Liste des spectacles
- GET `/shows/{id}` - Détails d'un spectacle
- GET `/shows/search` - Recherche de spectacles
- GET `/shows/category/{category}` - Filtrage par catégorie

### Administration
- GET `/admin/shows` - Gestion des spectacles
- GET/POST `/admin/shows/create` - Création de spectacle
- GET/POST `/admin/shows/{id}/edit` - Modification de spectacle
- POST `/admin/shows/{id}/delete` - Suppression de spectacle

### Réservation et paiement
- GET/POST `/shows/{id}/reserve` - Réservation
- GET/POST `/payment/{reservationId}` - Paiement
- GET `/payment/success` - Confirmation de paiement
- GET `/payment/error` - Erreur de paiement

### Tickets
- GET `/my-bookings` - Liste des réservations
- GET `/tickets/{reservationId}` - Affichage du ticket
- GET `/tickets/{reservationId}/download` - Téléchargement du ticket
- GET `/generate-tickets` - Génération de tickets de démonstration
