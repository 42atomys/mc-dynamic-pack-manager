GRADLE_VERSION = 8.8

build: update
	gradle build
run:
	gradle run
clean:
	gradle clean
test:
	gradle test
package:
	gradle package

update:
	bash ./update.sh

upgrade:
	bash ./upgrade_gradle_properties_to_latest.sh

gradle-update:
	@echo "Update gradle wrapper to version $(GRADLE_VERSION)"
	@bash ./upgrade_gradle.sh $(GRADLE_VERSION)

.PHONY: build run clean test package
