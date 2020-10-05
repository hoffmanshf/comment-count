# comment-count

## Assumptions

1. This program can only parse Java, TypeScript and Python files where Java and TypeScript files follow the same comment style.
2. This program can parse single file as well as multiple files
3. Comment-enabling characters that are part of strings will be parsed as comment.

    e.g.

    ```java
    /* inline comment */ public class Hello {
        String message = "text";
        String TODO = "// hello";
    }
    ```

    output

    ```
    Comment count for file: Hello.java
    Total # of lines: 4
    Total # of comment lines: 2
    Total # of single line comments: 1
    Total # of comment lines within block comments: 1
    Total # of block line comments: 1
    Total # of TODO’s: 0
    ```

4. Files with invalid filenames will be ignored and rest of valid files will be parsed. Error message will be thrown to stdout at runtime.
5. This program only parses single quote block comment for Python files. It does not handle double quote block comment. 

## Example

```bash
$ java CommentCount -help
  Usage: java CommentCount [options]
  -help                                :: display this help and exit.
  -file [filename1 filename2 ...]      :: indicate the [filename] to be parsed.

$ java CommentCount -file input/Application.java input/functions.py input/Student.ts
Comment count for file: input/functions.py
Total # of lines: 60
Total # of comment lines: 27
Total # of single line comments: 19
Total # of comment lines within block comments: 8
Total # of block line comments: 2
Total # of TODO’s: 3

Comment count for file: input/Application.java
Total # of lines: 60
Total # of comment lines: 28
Total # of single line comments: 6
Total # of comment lines within block comments: 22
Total # of block line comments: 2
Total # of TODO’s: 1

Comment count for file: input/Student.ts
Total # of lines: 40
Total # of comment lines: 23
Total # of single line comments: 5
Total # of comment lines within block comments: 18
Total # of block line comments: 4
Total # of TODO’s: 1

```
