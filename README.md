# AmethystVanish - Advanced Vanish Plugin

## 📖 Description
AmethystVanish is a feature-rich vanish plugin for Minecraft servers that goes beyond basic invisibility. With unique functionalities not found in other vanish plugins, it provides staff members with complete control over their visibility and interactions while vanished.

## ✨ Unique Features

### 🎭 Advanced Vanish System
- Complete Player Hiding – Become completely invisible to other players
- Permission-Based Visibility – Players with amethystvanish.see can see vanished staff
- Smart Join/Leave Messages – Customizable silent join messages for vanished players

### 🔧 Exclusive Functionalities
- Silent Chest Viewing – Open chests and containers without making any sound
- No Item Pickup – Prevent picking up items while vanished
- No Interaction Mode – Disable all block and entity interactions
- Custom Particle Effects – Visual effects while vanished (configurable)
- Custom Sound Effects – Unique sounds for vanish/reappear actions

### 🎮 User-Friendly Interface
- Interactive GUI – Easy-to-use visual interface for all features
- Real-time Status – Live updates showing current feature states
- Dynamic Placeholders – Real-time information in GUI lore

### 💾 Data Management
- Persistent Storage – SQLite (default) and MySQL support
- State Restoration – Automatically restores vanish state after relog
- Feature Memory – Remembers your preferred settings

## 🚀 Commands

### Main Commands
- /vanish – Toggle vanish on/off
- /v – Alias for /vanish

### Feature Commands
- /vanish gui – Open the vanish configuration GUI
- /vanish silentchest or /vanish sc – Toggle silent chest viewing
- /vanish nopickup or /vanish np – Toggle item pickup prevention
- /vanish nointeract or /vanish ni – Toggle interaction blocking
- /vanish reload – Reload plugin configuration

## 🔐 Permissions

### Core Permissions
- amethystvanish.use – Access to vanish command (default: op)
- amethystvanish.see – Ability to see vanished players (default: op)

### Feature Permissions
- amethystvanish.gui – Access to vanish GUI (default: op)
- amethystvanish.silentchest – Use silent chest feature (default: op)
- amethystvanish.nopickup – Use no pickup feature (default: op)
- amethystvanish.nointeract – Use no interact feature (default: op)
- amethystvanish.reload – Reload plugin configuration (default: op)

### Wildcard Permission
- amethystvanish.* – All permissions (default: op)

## ⚙️ Configuration

### Fully Customizable Files
- config.yml – Main plugin settings and database configuration
- messages.yml – All plugin messages and translations
- gui.yml – GUI layout, items, and appearance
- effects.yml – Particle and sound effects configuration

### Database Support (copy as YAML):
storage:
  type: "SQLITE" # or "MYSQL"
  mysql:
    host: "localhost"
    port: 3306
    database: "amethystvanish"
    username: "root"
    password: ""
    useSSL: false

## 🎯 Use Cases

### For Server Staff
- Moderators – Monitor players without being seen
- Administrators – Perform maintenance invisibly
- Builders – Work on projects without player interruption

### Special Scenarios
- Player Monitoring – Watch for rule breakers undetected
- Event Management – Organize events without distractions
- Technical Work – Fix issues without player interference

## 🔧 Installation
1. Download the latest AmethystVanish.jar
2. Place it in your server's plugins folder
3. Restart your server
4. Configure settings in plugins/AmethystVanish/
5. Use /vanish to get started!

## 📋 Requirements
- Minecraft Version: 1.16+ (tested on 1.18.2)
- Server Software: Spigot, Paper, or any Bukkit-based server
- Java Version: 8 or higher

## 🆕 Why Choose AmethystVanish?

### Unique Selling Points
- Features you won't find elsewhere – silent chest viewing, no pickup mode, etc.
- Professional quality – clean code, proper error handling, regular updates
- Highly customizable – every aspect can be modified
- Excellent performance – optimized for minimal server impact
- Active development – frequent feature updates

## 🏆 Compared to Other Vanish Plugins

Feature | AmethystVanish | Other Plugins
--------|----------------|--------------
Silent Chest Viewing | Yes | No
No Pickup Mode | Yes | Rare
No Interaction Mode | Yes | Rare
Custom Effects | Yes | Limited
Interactive GUI | Yes | Basic
MySQL Support | Yes | Yes

## 🤝 Support
GitHub Repository: [Your GitHub Link]  
Discord Support: [Your Discord Link]  
Issue Tracking: [Your Issue Tracker]

## 📄 License
This plugin is licensed under [Your License]. You may use it on your server and modify it for personal use. Redistribution requires permission.

## 🎉 Changelog

### Version 1.0.0
- Initial release
- All core features implemented
- SQLite and MySQL support
- Complete configuration system

Download now and experience the most advanced vanish system available!  
"Stay invisible, stay in control with AmethystVanish."
