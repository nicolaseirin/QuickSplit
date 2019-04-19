FROM microsoft/dotnet:sdk AS build-env
WORKDIR /app


# Copy csproj and restore as distinct layers
COPY *.sln ./
COPY QuickSplit.WebApi/*.csproj ./QuickSplit.WebApi/
COPY QuickSplit.Application/*.csproj ./QuickSplit.Application/
COPY QuickSplit.Persistence/*.csproj ./QuickSplit.Persistence/
COPY QuickSplit.Domain/*.csproj ./QuickSplit.Domain/
COPY QuickSplit.Test.Domain/*.csproj ./QuickSplit.Test.Domain/
COPY QuickSplit.Test.Application/*.csproj ./QuickSplit.Test.Application/

RUN dotnet restore

# Copy everything else and build
COPY . ./
RUN dotnet publish -c Release -o out

# Build runtime image
FROM microsoft/dotnet:aspnetcore-runtime
WORKDIR /app/
ENV ASPNETCORE_URLS=http://+:5050
ENV ConnectionStrings__QuickSplitDb=Server="quicksplitdb.c2l82w1gmwmb.sa-east-1.rds.amazonaws.com;Database=QuickSplitDb;Trusted_Connection=False;User ID=QuickSplit;Password=QuickSplit123;"
COPY --from=build-env /app/QuickSplit.WebApi/out .
ENTRYPOINT ["dotnet", "QuickSplit.WebApi.dll"]