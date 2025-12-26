use std::sync::{Arc, Mutex};
use std::thread;
use crate::serveur::server_thread::{ServerThread};

mod serveur;
mod app_defines;
mod types;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let messages = Arc::new(Mutex::new(Vec::new()));
    
    /*let server_messages = Arc::clone(&messages);
    thread::spawn(move || {
        let serv = ServerThread {
            address: "127.0.0.1".to_string(),
            port: 6969,
            messages: server_messages,
        };
        serv.start();
    });*/

    let serv = ServerThread::new(
        "127.0.0.1".to_string(),
        6969,
        messages,
    );

    serv.start();
    Ok(())
}
