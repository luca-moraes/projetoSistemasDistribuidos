cmake_minimum_required(VERSION 3.15)
project(MyProject)
set(CMAKE_CXX_STANDARD 17)

find_package(MPI REQUIRED)
include_directories(/usr/include/mpi/)
include_directories(${MPI_INCLUDE_PATH})
SET(CMAKE_C_COMPILER mpicc)
SET(CMAKE_CXX_COMPILER mpicxx)

add_executable(MyProject main.cpp)