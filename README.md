# LightDefy
Osram Lightify app

Allows seeing active lightbulbs and switching them on / off. More features are planned. Among others those are:
1. Renaming devices
2. Displaying other devices
3. Displaying and manipulating device groups
4. Displaying and manipulating scenes
5. Prettier UI


## How to use
Create "password.key" in the web service root directory with a password-string.

Then create password.xml in \app\src\main\res\values\password.xml containing the same password in the format:

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="password">thisisthesecretpassword</string>
    </resources>
