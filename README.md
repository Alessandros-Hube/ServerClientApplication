# Server-Client Chat Application

This Java-based application is a simple client-server chat system that allows multiple clients to communicate through a central server. The application includes a graphical user interface (GUI) for both the server and client to easily manage connections and messages.

## Overview

The application facilitates communication between multiple clients connected to a central server. The server manages connections, processes incoming messages, and broadcasts them to all connected clients. Each client can connect to the server, send messages, and see messages from other connected clients.

## Application Architecture

The application is divided into various modules, each serving a specific function:

- Server: Manages client connections and message distribution.
- Client: Sends and receives messages through the server connection.
- State: An Enum used to control status changes and synchronize communication across components.
- Observer Pattern: Key components use the Observer pattern to communicate real-time state changes.

## Component Description

### 1. Server Components

- ServerSock: Manages the server socket, accepts new connections, and organizes
- ClientHandler threads for each connected client.
- ClientHandler: Represents the connection to an individual client and processes its messages.
- ServerControl: Manages the overall server state, handles connections, and communicates with the GUI.
- ServerGUI: A graphical interface to control the server, display connected clients, and send messages to all clients.

### 2. Client Components

- ClientSocket: Establishes the network connection to the server and enables message sending and receiving.
- ClientControl: Manages the connection between the Client GUI and ClientSocket.
- ClientGUI: The client’s graphical interface, allowing users to send and receive messages.


## Installation and Execution

### Prerequisites

Java Development Kit (JDK): Version 8 or higher.

### Steps to Run

### 1. Start the Server:

- Compile the ServerSock, ServerControl, ClientHandler, and ServerGUI classes.
- Run ServerGUI to start the server.

### 2. Connect Clients:

- Compile the ClientSocket, ClientControl, and ClientGUI classes.
- Run ClientGUI, enter the server IP and port to connect.

## Usage

### 1. Server GUI:
- Start or stop the server with the on/off button.
- View a list of connected clients and their IP addresses.
- Send messages to all connected clients.
- Remove individual clients from the connection list as needed.

### 2. Client GUI:
- Connect to the server by entering the IP and port.
- Send messages to other clients on the network.
- View incoming messages from other clients in the chat box.

## Technical Details

### Communication

- The application uses TCP/IP sockets for communication between the server and clients.
- The Observer pattern ensures synchronization of states between the server and client components, enabling real-time updates.

## License

This application is licensed under the MIT License. For more details, see the LICENSE file.