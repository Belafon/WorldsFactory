using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Serilog;

namespace WorldsFactory.world.ids
{
	public class IDClassesManager : IIDConceptManager
	{
		public HashSet<Action<ConceptWithID>> OnClassCreated { get; private set; } = new HashSet<Action<ConceptWithID>>();
		public HashSet<Action<ConceptWithID>> OnClassDeleted { get; private set; } = new HashSet<Action<ConceptWithID>>();
		public Dictionary<string, ConceptWithID> AllIdClasses { get; private set; } = new Dictionary<string, ConceptWithID>();
		public ObservableCollection<ConceptWithID> AllItems { get; private set; } = new ObservableCollection<ConceptWithID>();

        private IIDConceptManager idConceptManager;
		
		public IDClassesManager(IIDConceptManager idConceptManager)
		{
			this.idConceptManager = idConceptManager;
			AllItems.CollectionChanged += (sender, args) =>
			{
				if (args.NewItems != null)
				{
					foreach (var item in args.NewItems)
					{
						if (item is not ConceptWithID concept
							|| !AllIdClasses.ContainsKey(concept.Id))
							throw new UnmodifiableCollectionChangedException("Attempt to add concept to AllClasses. Use AddObject instead.");
					}
				}
				if (args.OldItems != null)
				{
					foreach (var item in args.OldItems)
					{
						if (item is ConceptWithID concept
							&& AllIdClasses.ContainsKey(concept.Id))
							throw new UnmodifiableCollectionChangedException ("Attempt to remove concept from AllClasses. Use DeleteObject instead.");
					}
				}
			};
		}

		void IIDConceptManager.AddConcept(ConceptWithID obj)
		{
			if(AllIdClasses.ContainsKey(obj.Id))
				throw new ConceptWithIDAlreadyExistsException("Object with this id already exists");
			AllIdClasses.Add(obj.Id, obj);
			idConceptManager.AddConcept(obj);
			foreach (var action in OnClassCreated)
				action(obj);
			AllItems.Add(obj);
		}

		void IIDConceptManager.DeleteConcept(ConceptWithID obj)
		{
			if(!AllIdClasses.ContainsKey(obj.Id))
			{
				throw new ConceptWithIDAlreadyExistsException("Object with this id doesn't exist");
			}
			AllIdClasses.Remove(obj.Id);
			idConceptManager.DeleteConcept(obj);
			foreach (var action in OnClassDeleted)
				action(obj);
			AllItems.Remove(obj);
		}

		ConceptWithID? IIDConceptManager.GetConceptById(string id)
		{
			if(!AllIdClasses.ContainsKey(id))
				return idConceptManager.GetConceptById(id);
			return AllIdClasses[id];
		}

        bool IIDConceptManager.CanBeIdAdded(string id)
        {
			return !AllIdClasses.ContainsKey(id);
        }

        public bool DoesConceptExist(string id)
        {
            if (AllIdClasses.ContainsKey(id))
				return true;
			return idConceptManager.DoesConceptExist(id);
        }
    }
}