using System;
using System.IO;
using Domain.Infrastructure.FileServices;
using Domain.Infrastructure.Helpers.Exceptions;
using NUnit.Framework;

namespace Tests.Domain.Infrastructure.FileServices
{
    [TestFixture]
    public class FileServiceTests
    {
        private const string TestDirectory = "testDirectory";
        private const string TestFilePath = TestDirectory + "/test.txt";

        private IFileService _fileService;

        [SetUp]
        public void Setup()
        {
            _fileService = new FileService();
        }

        [Test]
        public void SaveDocContent_ShouldSaveFile()
        {
            using (var stream = new MemoryStream())
            {
                var result = _fileService.saveDocContent(TestFilePath, stream);

                Assert.IsTrue(result);
                Assert.IsTrue(File.Exists(TestFilePath));
            }
        }

        [Test]
        public void SaveDocContent_ShouldThrowFileAlreadyExistsException()
        {
            File.Create(TestFilePath).Dispose();

            using (var stream = new MemoryStream())
            {
                Assert.Throws<FileAlreadyExistsException>(() => _fileService.saveDocContent(TestFilePath, stream));
            }
        }

        [Test]
        public void LoadDocContent_ShouldLoadFile()
        {
            File.Create(TestFilePath).Dispose();

            var result = _fileService.loadDocContent(TestFilePath);

            Assert.IsNotNull(result);
        }

        [Test]
        public void LoadDocContent_ShouldThrowFileNotFoundException()
        {
            Assert.Throws<FileNotFoundException>(() => _fileService.loadDocContent(TestFilePath));
        }

        [Test]
        public void DeleteDoc_ShouldDeleteFile()
        {
            File.Create(TestFilePath).Dispose();

            var result = _fileService.deleteDoc(TestFilePath);

            Assert.IsTrue(result);
            Assert.IsFalse(File.Exists(TestFilePath));
        }

        [Test]
        public void DeleteDoc_ShouldThrowFileNotFoundException()
        {
            Assert.Throws<FileNotFoundException>(() => _fileService.deleteDoc(TestFilePath));
        }

        [Test]
        public void DeletePath_ShouldDeleteDirectory()
        {
            Directory.CreateDirectory(TestDirectory);

            var result = _fileService.deletePath(TestDirectory);

            Assert.IsTrue(result);
            Assert.IsFalse(Directory.Exists(TestDirectory));
        }

        [Test]
        public void DeletePath_ShouldThrowDirectoryNotFoundException()
        {
            Assert.Throws<DirectoryNotFoundException>(() => _fileService.deletePath(TestDirectory));
        }

        [Test]
        public void UpdateDocContent_ShouldUpdateFile()
        {
            File.Create(TestFilePath).Dispose();

            using (var stream = new MemoryStream())
            {
                var result = _fileService.updateDocContent(TestFilePath, stream);

                Assert.IsTrue(result);
                Assert.IsTrue(File.Exists(TestFilePath));
            }
        }

        [TearDown]
        public void TearDown()
        {
            if (Directory.Exists(TestDirectory))
            {
                Directory.Delete(TestDirectory, true);
            }
        }
    }
}
