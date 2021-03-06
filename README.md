# Description

Intellij plugin automatically fetching and displaying Geek & Poke comics
from [geekandpoke.typepad.com](http://geekandpoke.typepad.com/).


# Usage

After installation, a toolbar should appear displaying the list of postings as titles and an image for each one.
Updates are done automatically each 15 minutes, notification appears on each new fetched entry.
An update can be initiated manually by pressing the corresponding Refresh button, and more older items
can be downloaded by pressing the second one.

To view an entry in platform browser, double-click on an image.

Entries are cached locally in the plugin directory (which is expected to be writable), the cache can then
be reviewed and cleaned up in the IDE Settings.


# Known issues

Here's the list of things which have to be done sometime:

 *  On smaller screens, images can be too small making the text quite unreadable.
    To address this, we can implement full-screen preview of the comic using a platform viewer or separate panel.


# License

The code is released under Apache 2.0 license by Andy Belsky (andy@abelsky.com).
The latest sources are available at https://github.com/andy722/idea-comics
