@echo off
REM ─────────────────────────────────────────────────────────────────────────────
REM run-with-env.bat
REM ── Load every KEY=VALUE from .env into environment, then exec Java with args
REM ─────────────────────────────────────────────────────────────────────────────

REM 1. Locate .env (script dir or current dir)
if exist "%~dp0\.env" (
  set "ENV_FILE=%~dp0\.env"
) else if exist ".env" (
  set "ENV_FILE=.env"
) else (
  echo Warning: .env file not found.
  goto :run
)

REM 2. Read .env, skip lines starting with #, blank lines, and export
for /f "usebackq tokens=1* delims== eol=#" %%A in ("%ENV_FILE%") do (
  set "%%A=%%B"
)

:run
REM 3. Execute Java (or Maven) with whatever args you passed
REM    e.g. run-with-env.bat -jar target\myapp.jar
REM         run-with-env.bat mvn spring-boot:run
%*
