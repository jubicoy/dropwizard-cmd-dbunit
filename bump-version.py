#!/usr/bin/env python
#coding: utf-8

import sys
import os
import git
from subprocess import call

def main (args):
    if len(args) < 1:
        print('Invalid args')
        usage()

    repo = git.Repo(os.path.dirname(os.path.abspath(__file__)))

    newTag = verToStr(bumpVersion(getLatestTag(repo), args[0]))
    newSnapshot = ''
    if len(args) == 2:
        newSnapshot = verToStr(bumpVersion(newTag, args[1]))
    else:
        newSnapshot = verToStr(bumpVersion(newTag, 'patch'))

    newSnapshot = "{}-SNAPSHOT".format(newSnapshot)

    scheme = ''
    if os.path.isfile('pom.xml'):
        scheme = 'maven'
    else:
        print('Incompatible environment')
        exit(1)

    if not doPrompt(newTag, newSnapshot, scheme):
        print('Cancelled ...')
        exit(0)

    setVersion(repo, newTag, scheme)
    tagVersion(repo, newTag)
    setVersion(repo, newSnapshot, scheme)

def usage ():
    print('Usage:')
    print('    ./bump-version.py <tag bump> [<SNAPSHOT bump>]')
    print(' ')
    print('         tag bump: major|minor|patch bump to latest tag')
    print('    SNAPSHOT bump: major|minor|patch new tag to SNAPSHOT tag bump, default patch')
    exit(1)


def verToStr (ver):
    return '.'.join(map(str, ver))

def strToVer (verStr):
    return map(int, verStr.split('-')[0].split('.'))

def getLatestTag (repo):
    tagOut = repo.git.tag(l=True)
    if len(tagOut) == 0:
        return '0.0.0'

    return tagOut.split('\n')[-1]

def bumpVersion (version, bump):
    sw = {
        'major': lambda a: [ a[0] + 1, 0, 0 ],
        'minor': lambda a: [ a[0], a[1] + 1, 0 ],
        'patch': lambda a: [ a[0], a[1], a[2] + 1 ],
    }
    return sw.get(bump, lambda a: usage())(strToVer(version))

def setVersionMaven (repo, version):
    call(['mvn', 'versions:set', "-DnewVersion={}".format(version)])
    repo.index.add(['pom.xml'])

def setVersion (repo, version, scheme):
    schemeMap = {
        'maven': setVersionMaven,
    }
    schemeMap.get(scheme)(repo, version)
    repo.index.commit("Bumped version to {}".format(version))


def tagVersion (repo, version):
    new_tag = repo.create_tag(version, message="v{} Release".format(version))

def doPrompt (tag, snapshot, scheme):
    rsp = raw_input('Tag: {0}, Snapshot: {1}, Scheme: {2}. Continue? [y/N]: '.format(tag, snapshot, scheme))
    return rsp == 'y'

if __name__ == '__main__':
    exit(not main(sys.argv[1:]))
