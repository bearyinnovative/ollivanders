#-*- coding: utf-8 -*-
import os

from fabric.api import run, sudo, env, cd, local, prefix, put, lcd, settings
from fabric.contrib.files import exists
from fabric.contrib.project import rsync_project

user = 'deploy'
backends_dir = '/var/www/bearybots'
api_src_dir = './'
dist = 'debian'
host_count = len(env.hosts)
tmp_dir = '/tmp/bearybots_' + str(os.getpid())
initd_file = 'bearybots-api'

def _clean_local():
    local("rm -rf %s" % (tmp_dir))

def prepare_remote_dirs(prod = 'bearybots'):
    if not exists(backends_dir):
        sudo('mkdir -p %s' % backends_dir)
        sudo('chown %s %s' % (user, backends_dir))

def _prepare_local_api(target):
    local("cd %s && rm -rf ./target" % (api_src_dir))
    local("cd %s && rm -rf ./lib" % (api_src_dir))
    local("cd %s && lein jar" % (api_src_dir))
    local("cd %s && lein libdir" % (api_src_dir))
    local("mkdir -p %s/conf" % tmp_dir)
    local("mkdir -p %s/lib" % tmp_dir)
    local("cp -v %s/target/simple-web-service*.jar %s/lib/" % (api_src_dir, tmp_dir))
    local("cp -v %s/lib/*.jar %s/lib/" % (api_src_dir, tmp_dir))

def _start_on_boot(name, dist):
    if dist == 'debian':
        sudo('update-rc.d %s defaults' % name)
    elif dist == 'centos':
        sudo('/sbin/chkconfig --level 3 %s on' % name)
    else:
        raise ValueError('dist can only take debian, centos')

def deploy_api(prod='bearybots', target='prod'):
    global host_count,nginx_file,initd_file, backends_dir
    #set_up_dir(prod, target)
    if (host_count == len(env.hosts)):
        _prepare_local_api(target)
    prepare_remote_dirs(prod)
    rsync_project(local_dir=tmp_dir + '/',
                  remote_dir=backends_dir,
                  delete=True)
    sudo("chown -R %s:daemon %s" % (user,backends_dir))
    put('deploy/init.d/%s' % initd_file, '/etc/init.d/'  + initd_file,
        use_sudo=True, mode=0544)

    sudo("/etc/init.d/%s restart" % initd_file, pty=False)
    _start_on_boot(initd_file, dist)
    host_count -= 1
    if (host_count == 0):
       _clean_local()

def deploy_all(prod='bearybots', target='prod'):
    deploy_api(prod, target)
