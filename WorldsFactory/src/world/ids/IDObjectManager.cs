using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WorldsFactory.world.ids;

namespace WorldsFactory.world.ids
{
	public class IDObjectManager : IIDConceptManager
	{
		public HashSet<Action<ConceptWithID>> OnConceptCreated { get; private set; } = new HashSet<Action<ConceptWithID>>();
		public HashSet<Action<ConceptWithID>> OnConceptDeleted { get; private set; } = new HashSet<Action<ConceptWithID>>();
		public Dictionary<string, ConceptWithID> AllIdObjects { get; private set; } = new Dictionary<string, ConceptWithID>();
		public ObservableCollection<ConceptWithID> AllItems { get; private set; } = new ObservableCollection<ConceptWithID>();

        private IIDConceptManager idConceptManager;
		public IDObjectManager(IIDConceptManager idConceptManager)
		{
			this.idConceptManager = idConceptManager;
			AllItems.CollectionChanged += (sender, args) =>
			{
				if (args.NewItems != null)
				{
					foreach (var item in args.NewItems)
					{
						if (item is not ConceptWithID concept
							|| !AllIdObjects.ContainsKey(concept.Id))
							throw new UnmodifiableCollectionChangedException("Attempt to add concept to AllItems. Use AddObject instead.");
					}
				}
				if (args.OldItems != null)
				{
					foreach (var item in args.OldItems)
					{
						if (item is ConceptWithID concept
							&& AllIdObjects.ContainsKey(concept.Id))
							throw new UnmodifiableCollectionChangedException ("Attempt to remove concept from AllItems. Use DeleteObject instead.");
					}
				}
			};
		}
		
		void IIDConceptManager.AddConcept(ConceptWithID obj)
		{
			if(AllIdObjects.ContainsKey(obj.Id))
				throw new Exception("Object with this id already exists");
			AllIdObjects.Add(obj.Id, obj);
			idConceptManager.AddConcept(obj);	
			foreach (var action in OnConceptCreated)
			{
				action(obj);
			}
			AllItems.Add(obj);
		}

		void IIDConceptManager.DeleteConcept(ConceptWithID obj)
		{
			AllIdObjects.Remove(obj.Id);
			idConceptManager.DeleteConcept(obj);
			foreach (var action in OnConceptDeleted)
			{
				action(obj);
			}
			AllItems.Remove(obj);
		}

		ConceptWithID? IIDConceptManager.GetConceptById(string id)
		{
			if (!AllIdObjects.ContainsKey(id))
				return idConceptManager.GetConceptById(id);
			return AllIdObjects[id];
		}

        bool IIDConceptManager.CanBeIdAdded(string id)
        {
			return !AllIdObjects.ContainsKey(id);
        }

        public bool DoesConceptExist(string id)
        {
            if (AllIdObjects.ContainsKey(id))
				return true;
			else return idConceptManager.DoesConceptExist(id);
        }
    }
}