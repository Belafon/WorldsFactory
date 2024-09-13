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
                    //writer -> worldEditor "Writes"
                }
                projectManager -> worldEditor "Opens"

                worldObserver = component "World Observer" "Displays parts of the world"

                worldObserver -> worldEditor "Observes"
                //writer -> worldObserver "Observes"

                storyEditor = component "Story Editor" 

                storyEditor -> worldEditor "Observes"
                //writer -> storyEditor "Creates stories"
                
                storyInterpreterWork = component "Python Interpreter" {
                    description "Interprets pythons code"   
                }
                storyEditor -> storyInterpreterWork  "Uses"

                group "Works" {
                    bookWork = component "Book Editor" {
                        description "Editor for books writing"
                    }
                    bookWork -> storyEditor "Uses"

                    screenplayWork = component "Screenplay Editor" {
                        description "Editor for screenplays writing"
                    }
                    screenplayWork -> storyEditor "Uses" 
                
                    gamebookWork = component "Gamebook Editor" {
                        description "Editor for gamebooks writing"
                    }
                    gamebookWork -> storyEditor "Uses"
                }
                worldObserver -> StoryInterpreterWork "Uses"

                uiEnvironment = component "UI Environment" {
                    description "Creates environment for user interface"
                }

                uiEnvironment -> projectManager "Notifies after app boot"

                keyboardLayouts = component "Keyboard Layouts" {
                    description "Manages keyboard layouts with mappings"
                }

                uiEnvironment -> keyboardLayouts "Uses"
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