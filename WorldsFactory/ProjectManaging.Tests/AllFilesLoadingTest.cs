using System;
using System.IO;
using Xunit;
using System.Reflection;

namespace ProjectManaging.Tests
{
	/*
	public class AllFilesLoadingTest : IDisposable
	{
		private AllProjects allProjects;
		public AllFilesLoadingTest()
		{
			// Create the temporary files
			File.WriteAllText(AllProjects.KnownProjectsFileName, "");
			File.WriteAllText(AllProjects.RecentlyOpenedProjectsFileName, "");
			allProjects = AllProjects.getInstanceForTestingPurposesOnly();
		}

		[Fact]
		public void AddingNewProjectFromPathWillCallArgumentExceptionIfThePathDoesntExist()
		{
			string nonExistentPath = "non_existent_directory_path";
			Assert.Throws<ArgumentException>(() => allProjects.AddNewProjectFromDirectory(nonExistentPath));
		}

		public void Dispose()
		{
			// Delete the temporary files
			if (File.Exists(AllProjects.KnownProjectsFileName))
			{
				File.Delete(AllProjects.KnownProjectsFileName);
			}
			
			if (File.Exists(AllProjects.RecentlyOpenedProjectsFileName))
			{
				File.Delete(AllProjects.RecentlyOpenedProjectsFileName);
			}
		} 
			
		
	}*/
}
