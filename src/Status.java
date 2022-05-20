public enum Status {
    UNLAUNCHED,     // Before user launches in GUI
    LAUNCHING,      // After launched but before the bot is running
    SEARCHING,      // Bot is looking for a tree
    STOPPING,       // Bot is positioning itself to chop a found tree
    CHOPPING,       // Bot is chopping tree
    BACKTRACKING,   // Bot is walking backwards after having chopped the tree
    KILLED;         // Bot has been killed by user
}
