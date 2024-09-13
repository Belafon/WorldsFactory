workspace "WorldsFactory" {

    model {
        writer = person "Writer" {
            description "Creates stories"
        }

        properties {
            "structurizr.groupSeparator" "/"
        }

        worldsFactoryIDE = softwareSystem "WorldsFactory IDE" {
            description "Integrated development environment for creating and editing worlds."
            tags "IDE"
            
            WorldsFactory = container "WorldsFactory" {
                description "WorldsFactory is a software for creating and editing worlds."
                tags "WorldsFactory"
            
                projectManager = component "Project Manager" 

                group "World editor" {
                    libraryEditor = component "Library Editor" "Edits libraries of the world" 
                    eventEditor = component "Event Editor" "Edits events of the world" 
                    objectEditor = component "Object Editor" "Edits objects of the world" 
                    worldEditor = component "World Editor" "Edits events, objects and libraries of the world" 

                    worldEditor -> libraryEditor "Uses"
                    worldEditor -> eventEditor "Uses"
                    worldEditor -> objectEditor "Uses"
                }
                projectManager -> worldEditor "Opens"

                worldObserver = component "World Observer" "Displays parts of the world"

                worldObserver -> worldEditor "Observes"


                storyEditor = component "Story Editor" 

                storyEditor -> worldEditor "Observes"
                
                storyInterpreterWork = component "Python Interpreter" {
                    description "Interprets pythons code"   
                }
                storyEditor -> storyInterpreterWork  "Uses"

                uiEnvironment = component "UI Environment" {
                    description "Creates environment for user interface"
                }

                uiEnvironment -> projectManager "Notifies after app boot"
            }
        }

        group "Libraries" {
            javaLibraryWorldsFactory = softwareSystem "Java Library WorldsFactory" {
                description "Java library for interpreting stories"
                tags "Java Library"
            }
        }

        writer -> WorldsFactory "Writes"
        WorldsFactory -> writer "Delivers exported data"
        writer -> javaLibraryWorldsFactory "Gives exported data"
    }

    views {
        theme default
    }
}