<div align="center">
  <img src="https://raw.githubusercontent.com/taherzeiad/Beatly/main/art/logo.png" alt="Beatly Logo" width="120" height="120" style="border-radius: 24px;"/>
  
  # 🎵 Beatly

  **تطبيق أندرويد احترافي لإنشاء وتعديل الفيديوهات الموسيقية وعروض الصور المزامنة مع الإيقاع (Beats).**

  [![Android Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
  [![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
  [![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
  [![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

  <p align="center">
    <a href="#✨-المميزات">المميزات</a> •
    <a href="#🚀-بنية-المشروع-والهندسة">بنية المشروع</a> •
    <a href="#🛠️-التقنيات-المستخدمة">التقنيات</a> •
    <a href="#💻-التشغيل">طريقة التشغيل</a> •
    <a href="#📄-الترخيص">الترخيص</a>
  </p>
</div>

---

## 📱 نظرة عامة عن التطبيق

تطبيق **Beatly** هو أداة إبداعية متكاملة تتيح للمستخدمين، وصنّاع المحتوى، والمؤثرين تحويل صورهم ومقاطع الفيديو الخاصة بهم إلى فيديوهات احترافية قصيرة ومزامنتها تلقائياً مع النغمات والإيقاعات الموسيقية بكل سهولة وبأعلى جودة تصدير.

<div align="center">
  <img src="https://raw.githubusercontent.com/taherzeiad/Beatly/main/art/screenshot1.png" width="250" alt="Screen 1" style="margin: 10px; border-radius: 10px;" />
  <img src="https://raw.githubusercontent.com/taherzeiad/Beatly/main/art/screenshot2.png" width="250" alt="Screen 2" style="margin: 10px; border-radius: 10px;" />
</div>

---

## ✨ المميزات الرئيسية

* 🎼 **مزامنة ذكية للإيقاع:** دمج الانتقالات البصرية تلقائياً لتتوافق بدقة مع النغمات الموسيقية (Beat Sync).
* 🎬 **قوالب عصرية جاهزة:** مكتبة واسعة من القوالب الجاهزة بتأثيرات بصرية وانتقالات مذهلة.
* 📷 **صناعة عروض الصور (Slideshow):** تحويل الصور الثابتة إلى عروض مرئية ديناميكية متحركة.
* ⚡ **أداء عالي وسلس:** واجهات مستخدم مخصصة مبنية بالكامل بأحدث أدوات التطوير لضمان تجربة مستخدم سريعة وخفيفة.
* 💾 **تصدير بجودة عالية:** دعم تصدير الفيديوهات بدقة عالية HD دون خسارة في الجودة البصرية.

---

## 🛠️ التقنيات والمكتبات المستخدمة (Tech Stack)

تم بناء التطبيق باتباع أفضل الممارسات الموصى بها من قِبل Google لتطوير تطبيقات أندرويد حديثة ومستدامة:

* **لغة البرمجة:** [Kotlin](https://kotlinlang.org/) (حديثة، آمنة، ومتوافقة تماماً).
* **واجهة المستخدم (UI):** [Jetpack Compose](https://developer.android.com/jetpack/compose) لبناء واجهات برمجية تعريفية (Declarative UI) مرنة وسريعة.
* **إدارة الحالة والبيانات:** Coroutines & Flow للتفاعل غير المتزامن وإدارة تدفق البيانات وسلاسل العمليات الخلفية.
* **حقن الاعتمادية (Dependency Injection):** [Hilt / Dagger](https://developer.android.com/training/dependency-injection/hilt-android) لتسهيل فحص الأكواد وزيادة قابليتها لإعادة الاستخدام والـ Testing.
* **التنقل (Navigation):** Jetpack Compose Navigation للانتقال السلس بين الشاشات.
* **معالجة الصور والفيديو:** Coil لتحميل الصور بكفاءة، مع مكتبات مخصصة لمعالجة وتصدير الوسائط المتعددة.

---

## 🚀 بنية المشروع وهندسة البرمجيات (Architecture)

التطبيق يتبع نمط الهندسة النظيفة **(Clean Architecture)** مع تطبيق نمط **MVVM (Model-View-ViewModel)** لضمان فصل المهام وسهولة التوسع والصيانة:

```text
📊 app
 └── 📂 src
      └── 📂 main
           └── 📂 java/com/taherzeiad/beatly
                ├── 📂 data        # الطبقة الخاصة بالبيانات والـ Repositories ومصادر البيانات
                ├── 📂 domain      # طبقة العمليات الأساسية (Use Cases & Business Logic)
                └── 📂 ui          # طبقة العرض (Compose Screens, ViewModels, Themes)
                     ├── 📂 components
                     ├── 📂 screens
                     └── 📂 theme       # ألوان الخطوط وثيم التطبيق (Sugary White & New Air Force Blue)
