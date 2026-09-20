# Design Reflection

## AI use

I used Cursor to help type and tidy Java, then I changed the wording and the menu so it matches how I actually wanted the program to run. I can walk through every class without reading it off a prompt.

## Section 1: Class Design

I kept the layout close to the brief. `MtBullerAdmin` is just the console: print the menu, ask questions, catch bad input. `MtBullerResort` is the manager with three ArrayLists (rooms, customers, bundles). That split meant I was not dumping booking rules inside `main`.

`Customer` holds an id, a name, contact (email or phone, I did not overthink it), ski level, and a family list that can start empty. `FamilyMember` is name + level + its own id so option 7 and 8 can print `[id] Name (customer or family member)`.

`TravelBundle` is the actual holiday: who booked it, dates, who is tagging along, the room, then optional lift passes and lessons. The total is stay + passes (with the 10% rule) + lessons. I put the discount on the bundle because it cares about combined day-passes, not one person.

`Accommodation` is abstract because a hotel room, a lodge and an apartment are the same idea with different capacity. `Booking` is just a start date and a number of nights hanging off the room. If two ranges overlap, that room is busy.

`LiftPass` and `Lesson` are line items. If you add more later I update the same object (9 days then 2 more becomes 11) instead of printing two lines for Sam.

`BundleDatabase` is the JDBC/SQLite bit so option 9 and 10 are not mixed into the menu code.

I seeded ten rooms (HR01–AP04) and three people (Alice, Brian, David) so the marker is not staring at empty lists.

## Section 2: OOP Features

**Inheritance.** `HotelRoom`, `LodgeRoom` and `Apartment` extend `Accommodation`. Shared stuff (id, nightly price, bookings) lives in the parent. Capacity is 4, 2 and 6.

**Interface.** `Pricable` with `getPrice()` is on rooms, passes, lessons and the bundle. That was enough of an interface for the rubric without getting clever.

**Enums.** `SkiLevel` is BEGINNER / INTERMEDIATE / EXPERT and also stores lesson prices ($25 / $20 / $15). `LiftPassType` is DAY or SEASON so I was not comparing random strings.

**Exceptions.** `DateInPastException` is a runtime exception. Option 2 (and bundle dates) try to parse yyyy-MM-dd, throw if it is in the past, and the menu catch prints “The date cannot be in the past. Please try again”. Other bad input is mostly loops (“please enter a whole number between …”) which is boring but works.

## Section 3: Challenges

Creating a bundle was the messy one. You need a real customer, a date that is not yesterday and not three years away, 0–5 family members, then only rooms that fit the group and are free. If they pick AP01 I lock those dates on the apartment so the next person cannot steal it for the same week.

The lift pass math was annoying in a small way. $26 a day, 10% off once the family together has 5+ day-passes, $200 season as the other option. I show the raw line (8 day-passes = $208) but the bundle lift total uses the discount. If someone is about to spend close to $200 I print a heads up; I do not force a season pass because the sample still had 8 day-passes.

SQLite was fiddly (a bundle is a little tree of people and extras) but saving only bundles matches the brief. Customers without a bundle stay in memory only.

Word count: about 640.
