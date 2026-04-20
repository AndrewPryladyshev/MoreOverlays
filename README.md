## License

Please, read the LICENSE.txt file first.

# More Overlays

A powerful Android utility that allows you to create floating app overlays using Accessibility Services, enhancing your multitasking experience by keeping your favorite apps just a tap away.

---

## 🚀 Features

* **Multiple Overlays:** Support for up to **6 independent overlays**.
* **App Grids:** Display up to **4 apps** per overlay for quick access.
* **Dynamic Updates:** Overlays update in real-time. As soon as you select or change an app within the settings, the overlay reflects the change instantly.
* **Seamless Integration:** Designed to float over any active application.

---

## 🛠 Installation & Setup

To function correctly, **More Overlays** requires Accessibility Service permissions to draw over other apps and manage overlay positioning.

### Enabling Accessibility Service
1. Open the **More Overlays** app.
2. Tap the **"Enable Service"** button on the main screen.
3. This will automatically take you to your device's **Accessibility Settings**.
4. Find **More Overlays** in the list of installed services.
5. Toggle the switch to **On**.

> **Note:** Screenshots and a video tutorial on how to enable this service are coming soon!

---

## ⚠️ Important: HyperOS / MIUI Users

If you are using **HyperOS** (or MIUI), additional steps are required to ensure the system does not kill the overlay background process:

1. Long press the **More Overlays** app icon and go to **App Info**.
2. Navigate to **Battery Saver** (or Power settings).
3. Select **"No Restrictions"**.
4. Ensure "Display over other apps" is also granted in the permissions section.

*Failure to set "No Restrictions" may cause the overlays to disappear or the service to disconnect unexpectedly.*

---

## 📖 Usage

1. **Configure:** Open the app and select which apps you want in each of the 6 available slots.
2. **Interact:** Tap the overlay handles to expand/collapse your app shortcuts.
3. **Update:** Change your selection anytime; the overlays are dynamic and will refresh immediately.

---

## 🛠 Technical Details

* **Core:** Built using Kotlin and Android Accessibility Services.
* **Optimization:** Light memory footprint to ensure smooth performance even while multitasking.

---

## 🗺 Roadmap

- [ ] Video tutorial for setup.
- [ ] Customizable overlay themes and transparency.
- [ ] Increased limit for apps per overlay.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
