# Scientific Keyboard (Android)

a–z QWERTY কীবোর্ড, সাথে MS Word equation-এর মতো গাণিতিক চিহ্ন।

## চালানোর নিয়ম
1. Android Studio (Koala বা নতুন) দিয়ে এই ফোল্ডারটি **Open** করুন। প্রথমবার Gradle sync হতে ইন্টারনেট লাগবে।
   Android Studio যদি AGP/Gradle আপগ্রেড করতে বলে, "Update" চাপুন।
2. ফোন বা Emulator-এ **Run ▶** করুন।
3. অ্যাপ খুলে ধাপ ১ → ২ অনুযায়ী কীবোর্ড চালু করুন ও বেছে নিন।
   (Settings → System → Keyboard → On-screen keyboard থেকেও করা যায়।)

## ফাইল কোথায় কী
| ফাইল | কাজ |
|---|---|
| `SciKeyboardService.kt` | কীবোর্ডের মূল কোড (লেআউট, কী-হ্যান্ডলিং, শিফট, ব্যাকস্পেস, সুপার/সাবস্ক্রিপ্ট) |
| `SymbolData.kt` | চিহ্নের ক্যাটাগরি — এখানে নতুন চিহ্ন/ক্যাটাগরি যোগ করুন |
| `MainActivity.kt` | সেটআপ স্ক্রিন |
| `AndroidManifest.xml`, `res/xml/method.xml` | কীবোর্ড সার্ভিস রেজিস্ট্রেশন |

## পরিচিত সীমাবদ্ধতা
- কিছু ফোনে ফন্টে সব চিহ্ন না থাকলে বাক্স (□) দেখাতে পারে।
- সাজেশন/অটোকারেক্ট নেই; ভয়েস টাইপিং নেই।
