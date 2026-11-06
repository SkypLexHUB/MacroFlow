# MacroFlow

แอป Android Automation (Kotlin + Jetpack Compose + Material 3) ชื่อแพ็กเกจ `com.macroflow.automation` · minSdk 29

## สถานะการพัฒนา

**Milestone 1 เสร็จ (ยังไม่เคยผ่านการ Build จริง)** — โค้ดถูกเขียนและตรวจแบบ Static เท่านั้น ยังไม่เคยรัน Gradle, Unit Test หรือเปิดแอปบนเครื่องจริง ผลแรกที่เชื่อถือได้คือผลจาก GitHub Actions ครั้งแรกของคุณ ถ้า Build แดง ให้ส่ง Log มาแก้ได้เลย

### ที่ทำงานได้ใน Milestone 1 (ตามโค้ด)
- Navigation 5 แท็บ: หน้าหลัก, แมโคร, ตัวแก้ไข, ตั้งเวลา, ตั้งค่า
- Room Database (Macros, Macro Actions, Execution History) + DataStore (ธีม)
- แมโคร: สร้าง / แก้ไข / ลบ (มีกล่องยืนยัน) / ติดดาว / ค้นหา พร้อม Validation และข้อความ Error
- หน้าหลัก: สถิติจากฐานข้อมูล, กิจกรรมล่าสุด, แมโครโปรด
- ตั้งค่า: ธีม มืด/สว่าง/ตามระบบ, ล้างประวัติการรัน
- 2 ภาษาตามภาษาของเครื่อง: อังกฤษ และ ไทย
- Unit Test สำหรับ `MacroValidator`

### ยังไม่ทำ (Milestone ถัดไป)
- ตัวแก้ไข Action, Recorder, Player และ Accessibility Service (M2)
- Floating Widget, Profiles, Schedule, Workflow, Emergency Stop (M3)
- Cloud Sync, Sharing, Import/Export, Release Signing (M4)

แท็บ "ตัวแก้ไข" และ "ตั้งเวลา" ตอนนี้เป็นหน้า "เร็ว ๆ นี้" ตัวเลข "การรัน" บนหน้าหลักจะเป็น 0 จนกว่า Player (M2) จะบันทึกประวัติ

## อัปโหลดขึ้น GitHub

1. สร้าง Repository ใหม่บน GitHub
2. แตก ZIP แล้วอัปโหลดไฟล์ทั้งหมดขึ้น Branch `main` (ต้องมีโฟลเดอร์ `.github` ด้วย ซึ่งเป็นโฟลเดอร์ซ่อน)
3. ไปที่แท็บ **Actions** → Workflow **Build MacroFlow APK** (จะรันเองเมื่อ Push หรือกด **Run workflow**)
4. เมื่อเสร็จ เปิดการรัน แล้วดาวน์โหลดจากส่วน **Artifacts**:
   - `MacroFlow-Debug-APK` — ติดตั้งบนเครื่องได้ (เปิด "ติดตั้งจากแหล่งที่ไม่รู้จัก")
   - `MacroFlow-Release-APK-unsigned` — **ยังไม่ได้เซ็น** ติดตั้งไม่ได้จนกว่าจะตั้งค่า Signing ใน M4

## เรื่อง Gradle Wrapper

ZIP นี้ไม่มี `gradlew` และ `gradle-wrapper.jar` (เป็นไฟล์ที่ต้องดาวน์โหลดจากอินเทอร์เน็ต) Workflow จะสร้างให้เองอัตโนมัติบน GitHub Runner ถ้าจะ Build บนเครื่องตัวเอง ให้เปิดโปรเจกต์ด้วย Android Studio (Hedgehog ขึ้นไป, JDK 17) หรือรัน `gradle wrapper --gradle-version 8.7` หนึ่งครั้ง

## โครงสร้าง

```
app/src/main/java/com/macroflow/automation/
  data/local        Room: entity, dao, database
  data/repository   Macro, History, Settings (DataStore)
  domain            model, usecase, validation
  di                Hilt modules
  ui                theme, navigation, common, home, macros, settings
```

เวอร์ชันเครื่องมือ: Gradle 8.7 · AGP 8.5.2 · Kotlin 1.9.24 · Compose BOM 2024.06.00 · Hilt 2.51.1 · Room 2.6.1
