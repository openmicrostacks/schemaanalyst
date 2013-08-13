-- http://www.helenfeddema.com/Downloads.htm
CREATE TABLE 'tblAuthors' ('AuthorID' INTEGER, 'LastNameFirst' TEXT, 'LastName' TEXT, 'MiddleName' TEXT, 'FirstName' TEXT, 'StreetAddress' TEXT, 'City' TEXT, 'StateProvince' TEXT, 'PostalCode' TEXT, 'Country' TEXT, 'HomePhone' TEXT, 'Workphone' TEXT, 'FaxPhone' TEXT, 'BBSPhone' TEXT, 'CISName' TEXT, 'CISID' TEXT, 'EMailAddress' TEXT, 'WebSite' TEXT, 'Notes' TEXT);
CREATE TABLE 'tblBookAuthors' ('BookID' INTEGER, 'AuthorID' INTEGER);
CREATE TABLE 'tblBookSource' ('BookID' INTEGER, 'Source' TEXT);
CREATE TABLE 'tblBookSpecs' ('BookID' INTEGER, 'Specialty' TEXT);
CREATE TABLE 'tblBooks' ('BookID' INTEGER, 'Title' TEXT, 'TopicID' INTEGER, 'CopyrightYear' INTEGER, 'ISBNNumber' TEXT, 'Publisher' TEXT, 'PlaceOfPublication' TEXT, 'Translator' TEXT, 'PurchasePrice' TEXT, 'EditionNumber' INTEGER, 'CoverType' TEXT, 'DatePurchased' DATETIME, 'Pages' INTEGER, 'ShelfNumber' INTEGER, 'Notes' TEXT);
CREATE TABLE 'tblBooksAndVideos' ('BookID' INTEGER, 'Book' INTEGER, 'Video' INTEGER, 'Kit' INTEGER, 'Title' TEXT, 'ISBN' TEXT, 'Cover' TEXT, 'Language' TEXT, 'CD' INTEGER, 'Cost' DOUBLE, 'CurrencyType' TEXT, 'PublicationYear' TEXT, 'Length' TEXT, 'PublisherCode' TEXT, 'Copyright-free' INTEGER, 'Modified' DATETIME);
CREATE TABLE 'tblInfo' ('AppTitle' TEXT, 'PrimaryForm' TEXT, 'FormName' TEXT, 'ReportName' TEXT, 'Specialty' TEXT, 'ReportMode' INTEGER, 'FromDate' DATETIME, 'ToDate' DATETIME, 'NumCopies' INTEGER, 'PaperSize' INTEGER, 'Orientation' INTEGER);
CREATE TABLE 'tblPublisherSpecs' ('PublisherCode' TEXT, 'Specialty' TEXT);
CREATE TABLE 'tblPublishers' ('PublisherCode' TEXT, 'Pubname' TEXT, 'SalesStreetAddress' TEXT, 'SalesCity' TEXT, 'SalesProvince' TEXT, 'SalesPostcode' TEXT, 'SalesCountry' TEXT, 'EditorialStreetAddress' TEXT, 'EditorialCity' TEXT, 'EditorialProvince' TEXT, 'EditorialPostcode' TEXT, 'EditorialCountry' TEXT, 'GeneralPhone' TEXT, 'SalesPhone' TEXT, 'FaxPhone' TEXT, 'TechPhone' TEXT, 'BBSPhone' TEXT, 'CustServPhone' TEXT, 'Contact' TEXT, 'CISName' TEXT, 'CISID' TEXT, 'EMail' TEXT, 'WebSite' TEXT);
CREATE TABLE 'tblSourceSpecs' ('SourceID' TEXT, 'Specialty' TEXT);
CREATE TABLE 'tblSources' ('SourceID' TEXT, 'SourceName' TEXT, 'Retail' INTEGER, 'Wholesale' INTEGER, 'Contact' TEXT, 'StreetAddress' TEXT, 'City' TEXT, 'StateProvince' TEXT, 'PostalCode' TEXT, 'Country' TEXT, 'GeneralPhone' TEXT, 'SalesPhone' TEXT, 'FaxPhone' TEXT, 'TechPhone' TEXT, 'BBSPhone' TEXT, 'CustServPhone' TEXT, 'CISName' TEXT, 'Amex' INTEGER, 'MC' INTEGER, 'Visa' INTEGER, 'Discover' INTEGER, 'Diners' INTEGER, 'None' INTEGER, 'TechSupport' INTEGER, 'TechHours' TEXT, 'Catalog' INTEGER, 'CatCost' DOUBLE, 'CurrencyType' TEXT, 'Refundable' INTEGER, 'Showroom' INTEGER, 'Notes' TEXT, 'CISID' TEXT, 'EMail' TEXT, 'WebSite' TEXT, 'Modified' DATETIME);
CREATE TABLE 'tblSpecialties' ('Speccode' TEXT, 'Specdesc' TEXT);
CREATE TABLE 'tlkpForms' ('ObjectName' TEXT, 'DisplayName' TEXT, 'RecordSource' TEXT);
CREATE TABLE 'tlkpLetters' ('FileName' TEXT, 'DisplayName' TEXT);
CREATE TABLE 'tlkpReports' ('ObjectName' TEXT, 'DisplayName' TEXT, 'RecordSource' TEXT, 'Width' DOUBLE);
CREATE INDEX 'tblAuthors_AuthorID' ON 'tblAuthors' ('AuthorID' );
CREATE INDEX 'tblAuthors_CISID' ON 'tblAuthors' ('CISID' );
CREATE INDEX 'tblAuthors_PostalCode' ON 'tblAuthors' ('PostalCode' );
CREATE UNIQUE INDEX 'tblAuthors_PrimaryKey' ON 'tblAuthors' ('AuthorID' );
CREATE INDEX 'tblBookAuthors_AuthorID' ON 'tblBookAuthors' ('AuthorID' );
CREATE INDEX 'tblBookAuthors_ID' ON 'tblBookAuthors' ('BookID' );
CREATE INDEX 'tblBookSource_ID' ON 'tblBookSource' ('BookID' );
CREATE INDEX 'tblBookSource_{E5A6A8F2-B539-4213-978D-6E0A3EC8E3FE}' ON 'tblBookSource' ('Source' );
CREATE UNIQUE INDEX 'tblBookSpecs_PrimaryKey' ON 'tblBookSpecs' ('BookID' , 'Specialty' );
CREATE INDEX 'tblBookSpecs_{ABAF70F1-C900-40E1-8D34-C64CE492C5D5}' ON 'tblBookSpecs' ('Specialty' );
CREATE INDEX 'tblBookSpecs_{C5CB993E-CF32-4A64-BAB4-6654D373A163}' ON 'tblBookSpecs' ('BookID' );
CREATE UNIQUE INDEX 'tblBooksAndVideos_PrimaryKey' ON 'tblBooksAndVideos' ('BookID' );
CREATE INDEX 'tblBooksAndVideos_PublisherCode' ON 'tblBooksAndVideos' ('PublisherCode' );
CREATE INDEX 'tblBooksAndVideos_Title' ON 'tblBooksAndVideos' ('Title' );
CREATE UNIQUE INDEX 'tblBooks_PrimaryKey' ON 'tblBooks' ('BookID' );
CREATE INDEX 'tblBooks_PublisherName' ON 'tblBooks' ('Publisher' );
CREATE INDEX 'tblBooks_TopicID' ON 'tblBooks' ('TopicID' );
CREATE INDEX 'tblInfo_NumCopies' ON 'tblInfo' ('NumCopies' );
CREATE UNIQUE INDEX 'tblPublisherSpecs_PrimaryKey' ON 'tblPublisherSpecs' ('PublisherCode' , 'Specialty' );
CREATE INDEX 'tblPublisherSpecs_PublisherCode' ON 'tblPublisherSpecs' ('Specialty' );
CREATE INDEX 'tblPublisherSpecs_{9E32D73A-34C0-477F-A53F-21B4219CE562}' ON 'tblPublisherSpecs' ('PublisherCode' );
CREATE INDEX 'tblPublishers_CISID' ON 'tblPublishers' ('CISID' );
CREATE INDEX 'tblPublishers_EditorialPostcode' ON 'tblPublishers' ('EditorialPostcode' );
CREATE UNIQUE INDEX 'tblPublishers_PrimaryKey' ON 'tblPublishers' ('PublisherCode' );
CREATE INDEX 'tblPublishers_SalesPostcode' ON 'tblPublishers' ('SalesPostcode' );
CREATE UNIQUE INDEX 'tblSourceSpecs_PrimaryKey' ON 'tblSourceSpecs' ('SourceID' , 'Specialty' );
CREATE INDEX 'tblSourceSpecs_Specialty' ON 'tblSourceSpecs' ('Specialty' );
CREATE INDEX 'tblSourceSpecs_VendorID' ON 'tblSourceSpecs' ('SourceID' );
CREATE INDEX 'tblSources_CISID' ON 'tblSources' ('CISID' );
CREATE INDEX 'tblSources_Postcode' ON 'tblSources' ('PostalCode' );
CREATE UNIQUE INDEX 'tblSources_PrimaryKey' ON 'tblSources' ('SourceID' );
CREATE UNIQUE INDEX 'tblSpecialties_PrimaryKey' ON 'tblSpecialties' ('Speccode' );
