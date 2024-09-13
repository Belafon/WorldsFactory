zobrazovani prazdnych containers je chybne

chybne updateni zobrazovani objektu grouped by event




udelano 
ukazky projektu soucasti uzivatelske dokumentace
pridat ukazky projektu do aplikace

udelat
1076 TODO pridat obrazek


BUG ... java library, aktualne jmeno property setteru musi byt unikatni
... co kdyz dve ruzne tridy se stejnymi jmeny setteru
... Story ... setters hash table uklada metody setteru podle jmena

BUG ... talkWithJaku ... X ... talkWithJakub


{class class_Creature:
	def __init__(self, id):
		self._id = id
	
	properties = {
		"__object_name__": "@basicType:String",
		"location": "@class:Place",
		"informations": "@basicType:String",
		"inventory": "@class:Item"
	}
	@property
	def location(self):
		return self._location
		pass
	@location.setter
	def location(self, value):
		self._location = value
		set_property("location", value, self.properties["location"])
	
	informations = ArrayObject([10], "informations", "@basicType:String")
	inventory = ArrayObject([10], "inventory", "@class:Item")
	def __str__(self):
		return self._id
	
	def addInformation(self, information):
		logging.info("run method ---->   @class:Creature.@method:Creature_addInformation")
		try:
			return self._addInformation( information)
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Creature"
			method = "@method:Creature_addInformation"
			raise MethodRuntimeException("An Exception in @class:Creature during method @method:Creature_addInformation\n@class:Creature\n@method:Creature_addInformation\n" + message)
	
	def _addInformation(self, information):
		for i, value in enumerate(self.informations):
			if value is None:
				self.informations[i] = information
				break
		pass
		pass
	
	def isTypeOfItemInInventory(self, classType):
		logging.info("run method ---->   @class:Creature.@method:Creature_isTypeOfItemInInventory")
		try:
			return self._isTypeOfItemInInventory( classType)
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Creature"
			method = "@method:Creature_isTypeOfItemInInventory"
			raise MethodRuntimeException("An Exception in @class:Creature during method @method:Creature_isTypeOfItemInInventory\n@class:Creature\n@method:Creature_isTypeOfItemInInventory\n" + message)
	
	def _isTypeOfItemInInventory(self, classType):
		for item in self.inventory:
			if isinstance(item, classType):
				return True
		return False
		pass
}
