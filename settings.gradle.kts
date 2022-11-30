rootProject.name = "ru.cleverpumpkin.crunchycalendar"

include(":sample")
include(":crunchycalendar")

project(":sample").projectDir = File("sample")
project(":crunchycalendar").projectDir = File("crunchycalendar")