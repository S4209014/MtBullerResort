# Testing Evidence — Mt Buller Custom Travel Bundle System

| Test Case | Input | Expected Result | Actual Result | Pass/Fail |
| --- | --- | --- | --- | --- |
| Create customer | Valid name, contact, level 2 | Customer created (e.g. Customer #4) | Customer #4 printed | Pass |
| Create bundle | Duration = 5 days, valid future date, room ID | Bundle created | Bundle created with 5 day stay | Pass |
| Create bundle | Duration = -1 | Error, must be 1–60 | Prompt repeats / not accepted | Pass |
| Add lessons | Beginner, 1 lesson | Lesson cost $25 | $25.00 on the bundle | Pass |
| Startup seed | Run with no database | 10 rooms + Alice, Brian, David | Same as sample lists | Pass |
| Menu 1 | Choice 1 | All rooms HR01–AP04 with prices | Matches sample listing | Pass |
| Menu 4 | Choice 4 | Three default customers | Alice / Brian / David | Pass |
| Menu 2 past date | 2020-01-01 | "The date cannot be in the past. Please try again" | DateInPastException caught | Pass |
| Menu 2 nights | 2 nights, 2 people | Only rooms that fit and are free | Filtered list | Pass |
| Menu 2 too many people | 20 people | No rooms (max 6) | Friendly empty message | Pass |
| Add customer level | 3 | EXPERT | Level EXPERT | Pass |
| Bundle unknown customer | ID 99 | No customer with that ID | Error, no bundle | Pass |
| Family members | 2 names + levels | Family listed on bundle | Sam / Luca style line | Pass |
| Family max | 6 | Rejected, 0–5 only | Range message | Pass |
| Book AP01 | ID AP01 for 3 people | Attached, total = nightly × nights | e.g. $630 for 3 nights at $210 | Pass |
| Overlap booking | Same room same dates | Not in available list | Cannot attach | Pass |
| Lodge vs 3 people | 3 people | Lodges hidden | Only hotel/apartment | Pass |
| 5 day-passes | 5 days for one person | 10% off → $117.00 | Lift total $117.00 | Pass |
| 4 day-passes | 4 days | $104, no discount | $104.00 | Pass |
| Season pass | Choice 2 | $200 flat | $200.00 | Pass |
| Near season hint | 8 day-passes ($208) | Suggestion about $200 season pass, still stored as days | Hint printed, 8 days kept | Pass |
| Amend passes | 9 days then buy 2 more | Same person still one line, 11 days | One lift pass line | Pass |
| Intermediate lessons | 4 lessons | $80.00 | $80.00 | Pass |
| Expert lessons | 8 lessons | $120.00 | $120.00 | Pass |
| Save + read | Menu 9 then 10 | bundles.db then same bundles printed | Reloaded pretty print | Pass |
| Date 3 years ahead | Bundle start too far | 2 year limit message | Not accepted | Pass |

Re-run from IntelliJ (main class `com.resort.MtBullerAdmin`). Date examples use yyyy-MM-dd.
