use std::sync::{Arc, Mutex};
use eframe::egui;

use eframe::egui::{CentralPanel, Context, RichText, TopBottomPanel, Window};
use crate::app_defines::AppDefines;
use crate::ServerSettings;
use crate::types::StyledMessage;

/// A struct representing the server's user interface.
pub struct ServerUi {
    /// A thread-safe, shared vector of styled messages.
    messages: Arc<Mutex<Vec<StyledMessage>>>,
    /// Whether the 'About' dialog is currently shown.
    show_about: bool,
    /// Whether the 'Options' dialog is currently shown.
    show_options: bool,
    /// The width of the arena.
    arena_width: f32,
    /// The height of the arena.
    arena_height: f32,
    /// The probability of obstacles appearing in the arena.
    obstacle_probability: f64,
    /// The available game modes.
    game_modes: [&'static str; 1],
    /// The rate of fire for bots.
    bot_rate_of_fire: i32,
    /// The penalty time for infractions.
    penalty_time: i64,
    /// The delay before a connection times out.
    connection_timeout_delay: i32,
    /// The duration messages are displayed.
    message_duration: i32,
    /// The maximum length of a message.
    message_length: i32,
    /// The score limit for the game.
    score_limit: i32,
}

impl ServerUi {
    /// Creates a new `ServerUi` instance with the specified messages and settings.
    ///
    /// # Arguments
    ///
    /// * `messages` - A thread-safe, shared vector of styled messages.
    /// * `settings` - Thread-safe, shared server settings.
    ///
    /// # Returns
    ///
    /// A new `ServerUi` instance.
    ///
    pub fn new(messages: Arc<Mutex<Vec<StyledMessage>>>, settings: Arc<Mutex<ServerSettings>>) -> Self {
        ServerUi { messages, show_about: false, show_options: false,
            arena_width: AppDefines::ARENA_WIDTH,
            arena_height: AppDefines::ARENA_HEIGHT,
            obstacle_probability: AppDefines::OBSTACLE_PROBABILITY,
            game_modes: AppDefines::GAME_MODES,
            bot_rate_of_fire: AppDefines::BOT_RATE_OF_FIRE,
            penalty_time: AppDefines::PENALTY_TIME,
            connection_timeout_delay: AppDefines::CONNECTION_TIMEOUT_DELAY,
            message_duration: AppDefines::MESSAGE_DURATION,
            message_length: AppDefines::MESSAGE_LENGTH,
            score_limit: AppDefines::SCORE_LIMIT, }
    }

    /// Displays the main menu bar with options for general settings and help.
    ///
    /// # Arguments
    ///
    /// * `ctx` - The Egui context.
    ///
    fn show_menu(&mut self, ctx: &Context) {
        // TODO : create menu bar with "General" and "Help" menus
    }

    /// Displays the 'About' dialog with information about the application.
    ///
    /// # Arguments
    ///
    /// * `ctx` - The Egui context.
    ///
    fn show_about_dialog(&mut self, ctx: &Context) {
        // TODO : create 'About' dialog
    }

    /// Displays the 'Options' dialog for modifying game settings.
    ///
    /// # Arguments
    ///
    /// * `ctx` - The Egui context.
    ///
    fn show_options_dialog(&mut self, ctx: &Context) {
        // TODO : create 'Options' dialog with game settings
    }
}

impl eframe::App for ServerUi {
    /// Updates the server UI, showing the menu, about dialog, options dialog, and central panel with messages.
    ///
    /// # Arguments
    ///
    /// * `ctx` - The Egui context.
    /// * `_frame` - The Eframe frame.
    ///
    fn update(&mut self, ctx: &eframe::egui::Context, _frame: &mut eframe::Frame) {
        // TODO : implement the update method to show menu, dialogs, and messages

        // TODO : implement central panel to show messages
    }
}



