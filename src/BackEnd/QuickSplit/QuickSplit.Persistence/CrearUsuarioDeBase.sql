CREATE LOGIN QuickSplit WITH PASSWORD=N'QuickSplit123', DEFAULT_DATABASE = MASTER, DEFAULT_LANGUAGE = US_ENGLISH

GRANT CREATE ANY DATABASE TO QuickSplit;

CREATE USER QuickSplitUser FOR LOGIN QuickSplit

exec sp_addrolemember 'db_owner', 'QuickSplitUser'
