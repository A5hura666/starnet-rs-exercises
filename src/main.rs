use std::sync::{Arc, Mutex};
use std::thread;
use crate::serveur::server_thread::{ServerSettings, ServerThread};

mod serveur;
mod app_defines;
mod types;
mod ui;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Shared state for messages
    let messages = Arc::new(Mutex::new(Vec::new()));
    let settings = Arc::new(Mutex::new(ServerSettings::new()));

    // Clone the Arcs to move into the server thread
    let server_messages = Arc::clone(&messages);
    let server_settings = Arc::clone(&settings);

    // Start the server in a separate thread
    thread::spawn(move || {
        let serv = ServerThread::new("127.0.0.1".to_string(),6969, server_messages, server_settings);
        serv.start();
    });

    // Run the GUI in the main thread
    let native_options = eframe::NativeOptions::default();
    eframe::run_native(
        "Server GUI",
        native_options,
        Box::new(|_cc| Box::new(ui::server_ui::ServerUi::new(messages, settings))),
    ).expect("Failed to run server GUI");

    Ok(())
}
