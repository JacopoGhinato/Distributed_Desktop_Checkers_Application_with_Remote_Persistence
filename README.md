# DesktopCheckers  
**Distributed Desktop Checkers Application with Remote Persistence**

![Kotlin](https://img.shields.io/badge/Kotlin-JVM-blue)
![Compose Desktop](https://img.shields.io/badge/Compose-Desktop-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Remote%20Persistence-brightgreen)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-lightgrey)

---

## Overview

DesktopCheckers is a distributed desktop application developed using **Kotlin** and **Compose Desktop**.  
It enables two players, running on separate machines, to play a game of Checkers by sharing a remotely persisted game state stored in **MongoDB**.

The project was developed for the **Software Development Techniques (TDS)** course, following architectural patterns and design principles commonly adopted in professional and enterprise software systems.

---

## Objectives

- Provide a graphical, mouse-driven Checkers game
- Enable distributed play between two remote clients
- Persist game state remotely and consistently
- Apply clean architectural separation
- Demonstrate scalable and maintainable software design

---

## Functional Features

- Interactive checkers board with row and column identification
- Mouse-based move execution (select piece → select target)
- Game status bar displaying:
  - Game name
  - Current player
  - Active turn
- Game menu:
  - Start / Join Game
  - Refresh game state
  - Exit and cleanup
- Options menu:
  - Show possible target moves
  - Enable or disable automatic refresh
- Automatic background refresh while waiting for the opponent
- Safe handling of invalid moves and turn enforcement
- Remote persistence of game state using MongoDB

---

## User Interaction Flow

1. Click on a piece to select it
2. Click on a destination cell to perform the move
3. Illegal moves are rejected at the domain layer
4. UI updates automatically based on state changes

This interaction model follows standard UX patterns used in professional desktop applications.

---

## Architecture

The application follows a **layered MVVM architecture**:

## Distributed Behavior

- Game state is stored centrally in MongoDB
- Multiple clients synchronize through refresh operations
- Auto-refresh polls the database while waiting for the opponent
- Safe handling of concurrent reads and updates
- Game is deleted when a player exits to prevent stale state

## Technology Stack

- Kotlin (JVM)
- Compose Desktop
- MongoDB
- Kotlin Coroutines
- MVVM Architectural Pattern

## Testing & Validation

- Domain constraints enforce valid game states
- Invalid moves and illegal actions are rejected centrally
- UI reacts safely to all error conditions
