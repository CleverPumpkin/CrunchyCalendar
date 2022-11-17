rootProject.name = "ru.cleverpumpkin.crunchycalendar"

include(":sample")
include(":crunchycalendar")
include(":crunchycalendarcompose")

project(":sample").projectDir = File("sample")
project(":crunchycalendar").projectDir = File("crunchycalendar")
project(":crunchycalendarcompose").projectDir = File("crunchycalendarcompose")