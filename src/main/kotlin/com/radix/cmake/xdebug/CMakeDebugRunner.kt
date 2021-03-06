package com.radix.cmake.xdebug

import com.intellij.execution.ExecutionResult
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.radix.cmake.config.CMakeRunCommandLineState
import com.radix.cmake.config.CMakeRunConfiguration
import org.jetbrains.annotations.NotNull


class CMakeDebugRunner() : GenericProgramRunner <RunnerSettings>() {
    override fun canRun(p0: String, p1: RunProfile): Boolean {
        return true
    }

    override fun getRunnerId(): String {
        return "CMakeDebugRunner"
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        return super.doExecute(state, environment)
    }

    override fun doExecute(project: Project, state: RunProfileState, contentToReuse: RunContentDescriptor?,
                           environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()

        val runCommandLineState = state as CMakeRunCommandLineState
        val runConfig = runCommandLineState.environment.runProfile as CMakeRunConfiguration
        val debugPort = runConfig.port
        val serverProcessHandler = CMakeProcessFactory().createProcess(runConfig)
        val debuggerProxy = CMakeDebuggerProxy(debugPort)

        val session = XDebuggerManager.getInstance(project).startSession(environment,
                object : XDebugProcessStarter() {
            @NotNull
            override fun start(@NotNull session: XDebugSession): XDebugProcess {
                return CMakeDebugProcess(session, state, serverProcessHandler, debuggerProxy)
            }
        })
        return session.runContentDescriptor
    }
}

