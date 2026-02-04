use std::io::{BufRead, BufReader, BufWriter, Write};
use std::net::{Shutdown, TcpStream};
use std::sync::{Arc, Mutex};
use std::time::{SystemTime, UNIX_EPOCH};
use crate::app_defines::AppDefines;
use crate::types::{add_message, MessageType, StyledMessage};

/// A struct representing a client handler, responsible for communicating with a client via a TCP socket.
pub(crate) struct ClientHandler {
    /// The TCP socket associated with the client.
    pub(crate) socket: TcpStream,
    /// A buffer for writing data to the socket.
    pub(crate) buf_writer: BufWriter<TcpStream>,
    /// A buffer for reading data from the socket.
    pub(crate) buf_reader: BufReader<TcpStream>,
    /// The time in seconds since the Unix epoch of the client's last activity.
    pub(crate) previous_time: u64,
    /// A thread-safe, shared vector of styled messages.
    pub(crate) messages: Arc<Mutex<Vec<StyledMessage>>>,
}

impl ClientHandler {
    /// Creates a new client handler with the specified socket, messages, and server settings.
    ///
    /// # Arguments
    ///
    /// * `socket` - The client's TCP socket.
    /// * `messages` - A thread-safe, shared vector of styled messages.
    ///
    /// # Returns
    ///
    /// A new `ClientHandler`.
    ///
    pub fn new(socket: TcpStream,
               messages: Arc<Mutex<Vec<StyledMessage>>>,
        ) -> Self {
        // TODO : create buf_writer and buf_reader

        // TODO : create ClientHandler
    }

    /// Starts the client handler, reading messages from the client and processing them until disconnection or timeout.
    pub fn run(&mut self) {
        // TODO : read messages from client

        // TODO : process messages until disconnection or timeout
    }

    /// Checks if the client has exceeded the inactivity timeout.
    ///
    /// # Returns
    ///
    /// `true` if the client has exceeded the inactivity timeout, `false` otherwise.
    ///
    fn check_timeout(&mut self) -> bool {
        // TODO : check timeout
    }

    /// Handles a message received from the client.
    ///
    /// # Arguments
    ///
    /// * `received_message` - The received message as a string.
    ///
    fn handle_received_message(&mut self, received_message: &str) {
        // TODO : split messages and process each one
    }


    /// Processes an individual message from the client.
    ///
    /// # Arguments
    ///
    /// * `received` - The received message as a string.
    ///
    fn process_message(&mut self, received: &str) {
        // TODO : process message

        // TODO : send response

        // TODO : update previous_time
    }

    fn handle_disconnection(&mut self) {
        // TODO : handle disconnection
    }

    /// Adds a message to the response string.
    ///
    /// # Arguments
    ///
    /// * `response` - The existing response string.
    /// * `message` - The message to add to the response.
    ///
    /// # Returns
    ///
    /// The updated response string with the new message appended.
    ///
    pub fn add_to_reponse(mut reponse: String, message: String) {
        // TODO : add message to response
    }
}
