# GitHub repo commit comment viewer

Command line tool to list comments from GitHub repos

## Building

Just as any other gradle app run
```./gradlew assembleDist```

After that go to ```./build/distributions``` and unpack ```commit-comments-<VERSION>``` archive (tar or zip).

## Usage

Use start script from `/bin` as executable

./commit-comments <GitHub repo URL>

for information about any additional arguments just type ```./commit-comments -h```