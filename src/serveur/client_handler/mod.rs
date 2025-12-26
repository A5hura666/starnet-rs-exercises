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
        let buf_writer = BufWriter::new(socket.try_clone().unwrap());
        let buf_reader = BufReader::new(socket.try_clone().unwrap());
        ClientHandler {
            socket,
            buf_writer,
            buf_reader,
            previous_time: SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs(),
            messages,
        }
    }

    /// Starts the client handler, reading messages from the client and processing them until disconnection or timeout.
    pub fn run(&mut self) {
        let mut received_message = String::new();
        let mut running = true;
        while running {
            if self.check_timeout() {
                break;
            }

            if let Ok(message_length) = self.buf_reader.read_line(&mut received_message) {
                if message_length > 1 {
                    self.handle_received_message(&received_message);
                    received_message.clear();
                } else {
                    self.handle_disconnection();
                    running = false;
                    break;
                }
            }
        }
    }

    /// Checks if the client has exceeded the inactivity timeout.
    ///
    /// # Returns
    ///
    /// `true` if the client has exceeded the inactivity timeout, `false` otherwise.
    ///
    fn check_timeout(&mut self) -> bool {
        let now = SystemTime::now();
        let current_time = now.duration_since(UNIX_EPOCH).unwrap().as_secs();
        if current_time - self.previous_time > AppDefines::CONNECTION_TIMEOUT_DELAY as u64 {
            /*add_message(
                &self.messages,
                format!("[WARNING] Connection timeout: {}", self.socket.peer_addr().unwrap()),
                MessageType::Warning,
            );*/
            println!("[WARNING] Connection timeout: {}", self.socket.peer_addr().unwrap());
            self.socket.shutdown(std::net::Shutdown::Both).unwrap();
            true
        } else {
            false
        }
    }

    /// Handles a message received from the client.
    ///
    /// # Arguments
    ///
    /// * `received_message` - The received message as a string.
    ///
    fn handle_received_message(&mut self, received_message: &str) {
        let all_messages: Vec<&str> = received_message.trim().split(AppDefines::COMMAND_SEP).collect();
        for message in all_messages {
            println!("[INFO] Message : {:?}", message);
            match message {
                AppDefines::QUIT => {
                    self.handle_disconnection();
                    return;
                }
                _ => self.process_message(message),
            };
            self.previous_time = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs();
        }
    }


    /// Processes an individual message from the client.
    ///
    /// # Arguments
    ///
    /// * `received` - The received message as a string.
    ///
    fn process_message(&mut self, received: &str) {
        // On split d'abord sur le séparateur "=" pour récupérer le code et tous les arguments
        let mut parts = received.trim().split(AppDefines::ARGUMENT_SEP);
        let code = parts.next().unwrap_or("").trim();
        let args: Vec<&str> = parts.collect(); // Tous les arguments restants

        let response = match code {
            AppDefines::SET_NAME => {
                "SET NAME".to_string()
            }
            AppDefines::SET_COLOR => {
                "SET COLOR".to_string()
            }
            AppDefines::ALIVE => {
                "ALIVE".to_string()
            }
            AppDefines::MESSAGE => {
                "MESSAGE".to_string()
            }
            AppDefines::QUERY_CLOSEST_BOT => {
                "QUERY CLOSEST BOT".to_string()
            }
            AppDefines::QUERY_CLOSEST_PROJECTILE => {
                "QUERY CLOSEST PROJECTILE".to_string()
            }
            AppDefines::QUERY_BY_NAME => {
                "QUERY BY NAME".to_string()
            }
            AppDefines::QUERY_NAME_LIST => {
                "QUERY NAME LIST".to_string()
            }
            AppDefines::QUERY_ORIENTATION => {
                "QUERY ORIENTATION".to_string()
            }
            AppDefines::QUERY_MESSAGES_FROM_USER => {
                "QUERY MESSAGES FROM USER".to_string()
            }
            AppDefines::EMPTY_REPLY => {
                "EMPTY REPLY".to_string()
            }
            AppDefines::ACTUATOR_MOTOR_LEFT => {
                "ACTUATOR COMMAND".to_string()
            }
            AppDefines::ACTUATOR_MOTOR_RIGHT => {
                "ACTUATOR COMMAND".to_string()
            }
            AppDefines::ACTUATOR_GUN_TRIGGER => {
                "ACTUATOR COMMAND".to_string()
            }
            AppDefines::ACTUATOR_GUN_TRAVERSE => {
                "ACTUATOR COMMAND".to_string()
            }
            AppDefines::QUIT => {
                self.handle_disconnection();
                return;
            }
            /*AppDefines::SET_NAME => {
                if let Some(name) = args.get(0) {
                    let mut logic = self.game_logic.lock().unwrap();
                    if let Some(entity) = logic.get_entity_mut(entity_id) {
                        entity.set_name(name.to_string());
                        format!("Name set to {}", name)
                    } else {
                        "Entity not found".to_string()
                    }
                } else {
                    "Missing name".to_string()
                }
            }

            AppDefines::SET_COLOR => {
                if args.is_empty() {
                    "Missing color value".to_string()
                } else if args.len() == 1 {
                    // Cas couleur hexadécimale unique, ex: COL=FF00FF
                    if let Ok(hex) = u32::from_str_radix(args[0], 16) {
                        let r = ((hex >> 16) & 0xFF) as u8;
                        let g = ((hex >> 8) & 0xFF) as u8;
                        let b = (hex & 0xFF) as u8;
                        let mut logic = self.game_logic.lock().unwrap();
                        if let Some(entity) = logic.get_entity_mut(entity_id) {
                            entity.color = egui::Color32::from_rgb(r, g, b);
                            format!("Color set to RGB({}, {}, {})", r, g, b)
                        } else {
                            "Entity not found".to_string()
                        }
                    } else {
                        "Invalid color hex value".to_string()
                    }
                } else if args.len() == 3 {
                    // Cas RGB séparé par "=", ex: COL=255=234=234
                    if let (Ok(r), Ok(g), Ok(b)) = (
                        args[0].trim().parse::<u8>(),
                        args[1].trim().parse::<u8>(),
                        args[2].trim().parse::<u8>(),
                    ) {
                        let mut logic = self.game_logic.lock().unwrap();
                        if let Some(entity) = logic.get_entity_mut(entity_id) {
                            entity.color = egui::Color32::from_rgb(r, g, b);
                            format!("Color set to RGB({}, {}, {})", r, g, b)
                        } else {
                            "Entity not found".to_string()
                        }
                    } else {
                        "Invalid RGB values".to_string()
                    }
                } else {
                    "Invalid color format. Use hex or R=G=B".to_string()
                }
            }

            AppDefines::ACTUATOR_MOTOR_LEFT |
            AppDefines::ACTUATOR_MOTOR_RIGHT |
            AppDefines::ACTUATOR_GUN_TRIGGER |
            AppDefines::ACTUATOR_GUN_TRAVERSE => {
                if let Some(val_str) = args.get(0) {
                    match val_str.trim().parse::<f32>() {
                        Ok(val) => {
                            let mut logic = self.game_logic.lock().unwrap();
                            if let Some(ent) = logic.get_entity_mut(entity_id) {
                                match code {
                                    AppDefines::ACTUATOR_MOTOR_LEFT => ent.motor_left = val,
                                    AppDefines::ACTUATOR_MOTOR_RIGHT => ent.motor_right = val,
                                    AppDefines::ACTUATOR_GUN_TRIGGER => ent.gun_trigger = val,
                                    AppDefines::ACTUATOR_GUN_TRAVERSE => ent.gun_traverse = val,
                                    _ => {}
                                }
                                format!("{} set to {}", code, val)
                            } else {
                                "Entity not found".to_string()
                            }
                        }
                        Err(_) => "Invalid float value".to_string(),
                    }
                } else {
                    "Missing value".to_string()
                }
            }*/

            _ => format!("Unknown command: {}", code),
        };

        let _ = writeln!(self.buf_writer, "{}", response);
        let _ = self.buf_writer.flush();
    }

    fn handle_disconnection(&mut self) {
        let peer_addr = match self.socket.peer_addr() {
            Ok(addr) => addr,
            Err(_) => {
                println!("[WARN] Could not get peer address during disconnection.");
                /*add_message(
                  &self.messages,
                  "[WARN] Could not get peer address during disconnection.".to_string(),
                  MessageType::Warning,
                );*/
                return;
            }
        };

        println!("[INFO] Client disconnected: {:?}", Result::unwrap(self.socket.peer_addr()));
        self.socket.shutdown(Shutdown::Both).expect("Failed to shutdown socket");

        // Shutdown la socket, mais on ignore les erreurs bénignes
        if let Err(e) = self.socket.shutdown(Shutdown::Both) {
            println!("[WARN] Failed to shutdown socket for {}: {:?}", peer_addr, e);
            /*add_message(
                &self.messages,
                format!("[WARN] Failed to shutdown socket for {}: {:?}", peer_addr, e),
                MessageType::Warning,
            );*/
        }
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
        if reponse != "" {
            reponse += AppDefines::COMMAND_SEP;
            return reponse += &*message;
        }
    }
}
