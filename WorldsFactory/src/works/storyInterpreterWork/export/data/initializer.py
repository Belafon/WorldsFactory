class Objects:

    # Setter for variables
    def set_variable(self, name, value):
        setattr(self, name, value)

    # Getter for variables
    def get_variable(self, name):
        return getattr(self, name)
    
class Events:

    # Setter for variables
    def set_variable(self, name, value):
        setattr(self, name, value)

    # Getter for variables
    def get_variable(self, name):
        return getattr(self, name)


# for each event a singleton ->
class Singleton:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(Singleton, cls).__new__(cls)
            # Initialize the instance here
        return cls._instance
    